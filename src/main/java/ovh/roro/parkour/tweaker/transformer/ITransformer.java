package ovh.roro.parkour.tweaker.transformer;

import org.objectweb.asm.tree.ClassNode;

/**
 * @author roro1506_HD
 */
public interface ITransformer {

    String[] getClassName();

    void transform(ClassNode classNode, String name);

    default boolean nameMatches(String method, String... names) {
        for (String name : names) {
            if (method.equals(name))
                return true;
        }

        return false;
    }
}
