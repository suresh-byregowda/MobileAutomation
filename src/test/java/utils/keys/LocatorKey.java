
package utils.keys;

import utils.PageName;

/** Marker for typed keys that represent a page + a JSON locator key. */
public interface LocatorKey {
    PageName page();
    String key();     // exact JSON key (e.g., "Email_Input", "login")
}
