package ovh.roro.parkour.asm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import ovh.roro.parkour.asm.util.TransformerClass;
import ovh.roro.parkour.asm.util.TransformerMethod;
import ovh.roro.parkour.tweaker.transformer.ITransformer;

import java.util.ListIterator;

/**
 * @author roro1506_HD
 */
public class BlockPaneTransformer implements ITransformer {

    private final Logger logger = LogManager.getLogger("BlockPaneTransformer");

    @Override
    public String[] getClassName() {
        return new String[]{TransformerClass.BLOCK_PANE.getTransformerName()};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (TransformerMethod.CAN_PANE_CONNECT_TO.matches(method)) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                AbstractInsnNode target = null;
                boolean shouldAcquireTarget = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();

                    if (shouldAcquireTarget) {
                        target = node;
                        break;
                    }

                    if (node.getOpcode() == Opcodes.ASTORE && ((VarInsnNode) node).var == 5) { // Right after "Block block = world.getBlockState(off).getBlock();"
                        shouldAcquireTarget = true;
                    }
                }

                if (target == null) {
                    this.logger.error("Unable to find target to patch BlockPane");
                    return;
                }

                LabelNode const0Node = new LabelNode();

                method.instructions.insertBefore(target, new VarInsnNode(Opcodes.ALOAD, 5));
                method.instructions.insertBefore(target, new MethodInsnNode(Opcodes.INVOKESTATIC, "ovh/roro/parkour/asm/BlockPanePatch", "canPaneConnectToBlock", "(" + TransformerClass.BLOCK.getName() + ")Z", false));
                method.instructions.insertBefore(target, new JumpInsnNode(Opcodes.IFEQ, const0Node));

                method.instructions.add(const0Node);
                method.instructions.add(new InsnNode(Opcodes.ICONST_0));
                method.instructions.add(new InsnNode(Opcodes.IRETURN));
            }
        }
    }
}
