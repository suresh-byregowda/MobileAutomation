package pages.otg;

import base.BasePage;
import hooks.Hooks;
import utils.LocatorLogic;
import utils.keys.OnTheGoKey;

public class OtgProjectPage extends BasePage {

    private final LocatorLogic locator;

    public OtgProjectPage() {
        super(Hooks.getDriver());
        this.locator = new LocatorLogic(Hooks.getDriver());
    }

    public void verifyCurrentAllocation() {
        perform("Verify Current Allocation", () ->
                locator.one_ele(OnTheGoKey.CURRENT_ALLOCATION_DETAILS));
    }

    public void verifyPastAllocations() {
        perform("Verify Past Allocations", () ->
                locator.one_ele(OnTheGoKey.PAST_ALLOCATIONS));
    }

    public void verifyAllocationDetails() {
        verifyCurrentAllocation();
        verifyPastAllocations();
    }
}
