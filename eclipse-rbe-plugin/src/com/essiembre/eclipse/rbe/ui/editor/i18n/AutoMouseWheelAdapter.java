/*
 * Copyright (C) 2003-2014  Pascal Essiembre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.essiembre.eclipse.rbe.ui.editor.i18n;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

/**
 * Mouse Wheel Adapter
 */
public class AutoMouseWheelAdapter implements Listener {

    private int WM_VSCROLL;
    private int WM_HSCROLL;
    private int SB_LINEUP;
    private int SB_LINEDOWN;

    private Method fSendEventMethod32;
    private Method fSendEventMethod64;

    private Composite _parent;

    public AutoMouseWheelAdapter(Composite parent) {
        if (!SWT.getPlatform().equals("win32")) {
            return;
        }

        _parent = parent;

        try {
            Class<?> os = Class.forName("org.eclipse.swt.internal.win32.OS");
            WM_VSCROLL = os.getDeclaredField("WM_VSCROLL").getInt(null);
            WM_HSCROLL = os.getDeclaredField("WM_HSCROLL").getInt(null);
            SB_LINEUP = os.getDeclaredField("SB_LINEUP").getInt(null);
            SB_LINEDOWN = os.getDeclaredField("SB_LINEDOWN").getInt(null);

            try {
                // Try the 32-bit version first
                fSendEventMethod32 = os.getDeclaredMethod("SendMessage",
                        int.class, int.class, int.class, int.class);
            } catch (NoSuchMethodException e) {
                // Fall back to the 64-bit version
                fSendEventMethod64 = os.getDeclaredMethod("SendMessage",
                        long.class, int.class, long.class, long.class);
            }

            Display.getDefault().addFilter(SWT.MouseWheel, this);
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Warning: Running on win32 SWT platform, "
                + "but unable to install Win32MouseWheelFilter filter.");
    }

    public final void dispose() {
        Display.getDefault().removeFilter(SWT.MouseWheel, this);
    }

    @Override
    public final void handleEvent(Event event) {
        Control cursorControl = event.display.getCursorControl();
        if (event.widget == cursorControl || cursorControl == null) {
            return;
        }

        if (event.widget instanceof Control) {
            Control control = (Control) event.widget;
            Rectangle bounds = control.getBounds();
            bounds.x = 0;
            bounds.y = 0;
            Point cursorPos = control.toControl(event.display
                    .getCursorLocation());
            if (bounds.contains(cursorPos)) {
                return;
            }
        }

        Control wheelControl = cursorControl;
        int scrollStyle = SWT.V_SCROLL;
        while (wheelControl != null
                && (wheelControl.getStyle() & scrollStyle) == 0) {
            wheelControl = wheelControl.getParent();
        }
        if (wheelControl == null) {
            return;
        }

        if (!hasCorrectParent(wheelControl))
            return;

        int style = wheelControl.getStyle();

        if ((style & scrollStyle) != 0 && wheelControl instanceof Scrollable) {
            int msg;

            if ((style & SWT.V_SCROLL) != 0) {
                ScrollBar vBar = ((Scrollable) wheelControl).getVerticalBar();
                if (vBar == null || ((vBar.getMinimum() == 0 
                        && vBar.getMaximum() == 0 && vBar.getSelection() == 0) 
                                || !vBar.isEnabled() || !vBar.isVisible())) {
                    msg = WM_HSCROLL;
                } else {
                    msg = WM_VSCROLL;
                }
            } else {
                msg = WM_HSCROLL;
            }

            int count = event.count;
            int wParam = SB_LINEUP;
            if (event.count < 0) {
                count = -count;
                wParam = SB_LINEDOWN;
            }

            try {
                if (fSendEventMethod32 != null) {
                    int handle = org.eclipse.swt.widgets.Control.class
                            .getDeclaredField("handle").getInt(wheelControl);
                    for (int i = 0; i < count; i++) {
                        fSendEventMethod32.invoke(null, handle, msg, wParam, 0);
                    }
                } else {
                    long handle = org.eclipse.swt.widgets.Control.class
                            .getDeclaredField("handle").getLong(wheelControl);
                    for (int i = 0; i < count; i++) {
                        fSendEventMethod64.invoke(null, handle, msg, wParam, 0);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Point cursorPos = wheelControl.toControl(event.display
                    .getCursorLocation());
            event.x = cursorPos.x;
            event.y = cursorPos.y;

            event.widget = wheelControl;
            wheelControl.notifyListeners(event.type, event);
        }

        event.type = SWT.None;
        event.doit = false;
    }

    private boolean hasCorrectParent(Control control) {
        for (; control != null; control = control.getParent()) {
            if (control.equals(_parent))
                return true;
        }
        return false;
    }

}
