package ovh.roro.parkour.asm.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import ovh.roro.parkour.tweaker.BarrierPanePatcherTransformer;

/**
 * @author roro1506_HD
 */
public enum TransformerMethod {

    CAN_PANE_CONNECT_TO("canPaneConnectTo", "canPaneConnectTo", "canPaneConnectTo", "(" + TransformerClass.I_BLOCK_ACCESS.getName() + TransformerClass.BLOCK_POS.getName() + TransformerClass.ENUM_FACING.getName() + ")Z"),

    // Constructor
    INIT("<init>", "<init>", "<init>", "()V"),

    NULL(null, null, null, null, false);

    private final String name;
    private final String description;
    private final String[] exceptions;

    TransformerMethod(String deobfMethod, String seargeMethod, String notchMethod18, String seargeDescription) {
        this(deobfMethod, seargeMethod, notchMethod18, seargeDescription, seargeDescription, false);
    }

    TransformerMethod(String deobfMethod, String seargeMethod, String notchMethod18, String seargeDescription, String notchDescription) {
        this(deobfMethod, seargeMethod, notchMethod18, seargeDescription, notchDescription, false);
    }

    TransformerMethod(String deobfMethod, String seargeMethod, String notchMethod18, String seargeDescription, boolean ioException) {
        this(deobfMethod, seargeMethod, notchMethod18, seargeDescription, seargeDescription, ioException);
    }

    TransformerMethod(String deobfMethod, String seargeMethod, String notchMethod18, String seargeDescription, String notchDescription, boolean ioException) {
        if (BarrierPanePatcherTransformer.isDeobfuscated()) {
            this.name = deobfMethod;
            this.description = seargeDescription;
        } else {
            if (BarrierPanePatcherTransformer.isUsingNotchMappings()) {
                this.name = notchMethod18;
                this.description = notchDescription;
            } else {
                this.name = seargeMethod;
                this.description = seargeDescription;
            }
        }

        if (ioException)
            this.exceptions = new String[]{"java/io/IOException"};
        else
            this.exceptions = new String[0];
    }

    public String getName() {
        return this.name;
    }

    public MethodNode createMethodNode() {
        return new MethodNode(Opcodes.ACC_PUBLIC, this.name, this.description, null, this.exceptions);
    }

    public boolean matches(MethodInsnNode methodInsnNode) {
        return this.name.equals(methodInsnNode.name) && this.description.equals(methodInsnNode.desc);
    }

    public boolean matches(MethodNode methodNode) {
        return this.name.equals(methodNode.name) && (this.description.equals(methodNode.desc) || this == INIT);
    }
}
