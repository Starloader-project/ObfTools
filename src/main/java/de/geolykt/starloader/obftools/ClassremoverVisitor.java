package de.geolykt.starloader.obftools;

import java.util.HashSet;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassremoverVisitor extends ClassVisitor {

    protected final String annotation;
    protected final HashSet<Map.Entry<String, String>> methodBlackList = new HashSet<>();

    public ClassremoverVisitor(int api, ClassVisitor classVisitor, String annotation, ClassNode original) {
        super(api, classVisitor);
        this.annotation = annotation;
        for (MethodNode method : original.methods) {
            boolean skip = false;
            if (method.invisibleAnnotations != null) {
                for (AnnotationNode annotationNode : method.invisibleAnnotations) {
                    if (annotationNode.desc.equalsIgnoreCase(annotation)) {
                        methodBlackList.add(Map.entry(method.name, method.desc));
                        skip = true;
                        break;
                    }
                }
            }
            if (!skip && method.visibleAnnotations != null) {
                for (AnnotationNode annotationNode : method.visibleAnnotations) {
                    if (annotationNode.desc.equalsIgnoreCase(annotation)) {
                        methodBlackList.add(Map.entry(method.name, method.desc));
                        skip = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
            String[] exceptions) {
        if (methodBlackList.contains(Map.entry(name, descriptor))) {
            return null;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
