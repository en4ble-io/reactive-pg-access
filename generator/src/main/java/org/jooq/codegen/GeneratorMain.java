package org.jooq.codegen;

import java.io.File;

public class GeneratorMain {
    public static void generate(String xml, File projectDir) throws Exception {
        System.setProperty("projectDir", projectDir.getAbsolutePath());
        GenerationTool.generate(xml);
    }

}
