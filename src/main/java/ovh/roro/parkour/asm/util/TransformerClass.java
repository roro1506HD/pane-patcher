package ovh.roro.parkour.asm.util;

import ovh.roro.parkour.tweaker.BarrierPanePatcherTransformer;

/**
 * @author roro1506_HD
 */
public enum TransformerClass {
    BLOCK_PANE("net/minecraft/block/BlockPane", "akd"),
    BLOCK("net/minecraft/block/Block", "afh"),
    I_BLOCK_ACCESS("net/minecraft/world/IBlockAccess", "adq"),
    BLOCK_POS("net/minecraft/util/BlockPos", "cj"),
    ENUM_FACING("net/minecraft/util/EnumFacing", "cq"),

    NULL(null, null);

    private final String name;
    private final String seargeClass;

    TransformerClass(String seargeClass, String notchClass18) {
        this.seargeClass = seargeClass;

        if (BarrierPanePatcherTransformer.isDeobfuscated() || !BarrierPanePatcherTransformer.isUsingNotchMappings()) {
            this.name = seargeClass;
        } else {
            this.name = notchClass18;
        }
    }

    /**
     * @return The name used for the owner of a field or method, or a field type.
     */
    public String getNameRaw() {
        return this.name;
    }

    /**
     * @return The name used in a method descriptor to represent an object.
     */
    public String getName() {
        return "L" + this.name + ";";
    }

    public String getTransformerName() {
        return this.seargeClass.replaceAll("/", ".");
    }
}
