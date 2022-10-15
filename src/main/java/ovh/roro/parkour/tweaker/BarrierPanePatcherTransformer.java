package ovh.roro.parkour.tweaker;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import ovh.roro.parkour.asm.BlockPaneTransformer;
import ovh.roro.parkour.tweaker.transformer.ITransformer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * @author roro1506_HD
 */
public class BarrierPanePatcherTransformer implements IClassTransformer {

    private final static boolean OUTPUT_BYTECODE = true;

    private static boolean DEOBFUSCATED;
    private static final boolean USING_NOTCH_MAPPINGS;

    private final Logger logger = LogManager.getLogger("BarrierPanePatcher Transformer");
    private final Multimap<String, ITransformer> transformerMap = ArrayListMultimap.create();

    public BarrierPanePatcherTransformer() {
        this.registerTransformer(new BlockPaneTransformer());
    }

    private void registerTransformer(ITransformer transformer) {
        for (String className : transformer.getClassName())
            this.transformerMap.put(className, transformer);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null)
            return null;

        Collection<ITransformer> transformers = this.transformerMap.get(transformedName);

        if (transformers.isEmpty())
            return basicClass;

        this.logger.info("Found {} transformers for {}", transformers.size(), transformedName);

        ClassReader reader = new ClassReader(basicClass);
        ClassNode classNode = new ClassNode();

        reader.accept(classNode, ClassReader.EXPAND_FRAMES);

        for (ITransformer transformer : transformers) {
            this.logger.info("Applying transformer {} on {}...", transformer.getClass().getName(), transformedName);
            transformer.transform(classNode, transformedName);
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        try {
            classNode.accept(writer);
        } catch (Throwable throwable) {
            this.logger.error("Exception when transforming {}: {}", transformedName, throwable.getClass().getSimpleName());
            throwable.printStackTrace();
            this.outputBytecode(transformedName, writer);
            return basicClass;
        }

        this.outputBytecode(transformedName, writer);

        return writer.toByteArray();
    }

    private void outputBytecode(String transformedName, ClassWriter writer) {
        if (BarrierPanePatcherTransformer.OUTPUT_BYTECODE) {
            try {
                File file = new File("E:\\Desktop\\bytecode", transformedName + ".class");
                if (file.getParentFile().exists()) {
                    file.createNewFile();

                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(writer.toByteArray());
                    outputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static boolean isDeobfuscated() {
        return BarrierPanePatcherTransformer.DEOBFUSCATED;
    }

    public static boolean isUsingNotchMappings() {
        return BarrierPanePatcherTransformer.USING_NOTCH_MAPPINGS;
    }

    static {
        BarrierPanePatcherTransformer.DEOBFUSCATED = false;
        try {
            // DEOBFUSCATED = (boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
            Class<?> launch = Class.forName("net.minecraft.launchwrapper.Launch");
            Field blackboardField = launch.getField("blackboard");
            Map<String, Object> blackboard = (Map<String, Object>) blackboardField.get(null);
            BarrierPanePatcherTransformer.DEOBFUSCATED = (boolean) blackboard.get("fml.deobfuscatedEnvironment");
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ex) {
            // If the class doesn't exist, its probably just obfuscated labymod client, so leave it false.
        }

        USING_NOTCH_MAPPINGS = !BarrierPanePatcherTransformer.DEOBFUSCATED;
    }
}
