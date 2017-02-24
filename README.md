ResourceBundle Editor
===========

[![Join the chat at https://gitter.im/essiembre/eclipse-rbe](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/essiembre/eclipse-rbe?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)


Eclipse plugin for editing Java resource bundles. Lets you manage all localized .properties files in one screen. Some features: sorted keys, warning icons on missing keys/values, conversion to/from Unicode, hierarchical view of keys, and more.


<img src="http://essiembre.github.io/eclipse-rbe/img/screenshots/main-screen.png">

Go to ResourceBundle Editor web site for more screenshots and other information: http://essiembre.github.io/eclipse-rbe/


How to install
--------------

**Marketplace:**

*just drag to install* - you have to drag the image below to your Eclipse workspace

<a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=2628188" class="drag" title="Drag to your running Eclipse workspace to install ResourceBundle Editor"><img src="https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.png" alt="Drag to your running Eclipse workspace to install ResourceBundle Editor" /></a>

or visit page: [ResourceBundle Editor on Marketplace](https://marketplace.eclipse.org/content/resourcebundle-editor)


**Update Site:**

Create a new update site in Eclipse with the following:

* Site name:  ``ResourceBundle Editor``
* Site URL:   ``https://raw.githubusercontent.com/essiembre/eclipse-rbe/master/eclipse-rbe-update-site/site.xml``


**Manual Install:**

Download the plugin jar and copy it to Eclipse plugins directory. The jar location is (replace ``<version>``): 
``https://raw.githubusercontent.com/essiembre/eclipse-rbe/master/eclipse-rbe-update-site/plugins/com.essiembre.eclipse.rbe_<version>.jar``.   Alternatively, you can download the entire Update Site bundled with everything else in the release link above and create a new update site in Eclipse, pointing to your local directory where you expanded the release.
