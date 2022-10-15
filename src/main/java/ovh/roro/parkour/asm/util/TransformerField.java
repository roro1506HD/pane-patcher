package ovh.roro.parkour.asm.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import ovh.roro.parkour.tweaker.BarrierPanePatcherTransformer;

/**
 * @author roro1506_HD
 */
public enum TransformerField {
    NULL(null, null, null, null);

    private final String name;
    private final String type;

    TransformerField(String deobfName, String seargeName, String notchName18, String type) {
        this.type = type;

        if (BarrierPanePatcherTransformer.isDeobfuscated()) {
            this.name = deobfName;
        } else {
            if (BarrierPanePatcherTransformer.isUsingNotchMappings())
                this.name = notchName18;
            else
                this.name = seargeName;
        }
    }

    public String getName() {
        return this.name;
    }

    public FieldInsnNode getField(TransformerClass currentClass) {
        return new FieldInsnNode(Opcodes.GETFIELD, currentClass.getNameRaw(), this.name, this.type);
    }

    public FieldInsnNode getStatic(TransformerClass currentClass) {
        return new FieldInsnNode(Opcodes.GETSTATIC, currentClass.getNameRaw(), this.name, this.type);
    }

    public FieldInsnNode putField(TransformerClass currentClass) {
        return new FieldInsnNode(Opcodes.PUTFIELD, currentClass.getNameRaw(), this.name, this.type);
    }
}
