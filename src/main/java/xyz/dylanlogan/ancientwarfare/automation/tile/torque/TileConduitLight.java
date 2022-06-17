package xyz.dylanlogan.ancientwarfare.automation.tile.torque;

import xyz.dylanlogan.ancientwarfare.automation.config.AWAutomationStatics;

public final class TileConduitLight extends TileTorqueSidedCell {

    @Override
    protected double getEfficiency() {
        return AWAutomationStatics.low_efficiency_factor;
    }

    @Override
    protected double getMaxTransfer() {
        return AWAutomationStatics.low_transfer_max;
    }

}