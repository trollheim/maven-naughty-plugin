package net.trollheim.appsec.utils;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * File visitor that finds main class
 */
public class FindMainClassVisitor extends SimpleFileVisitor<Path> {

    /**
     * get collected paths
     * @return
     */
    public Set<String> getPaths() {
        return paths;
    }

    /**
     * Collected paths
     */
    private Set<String> paths = new HashSet<>();

    /**
     * expected keywords
     */
    private static final List<String> expected = Arrays.asList("public", "static", "void", "main");


    /**
     * visit file
     * @param path path
     * @param attrs attributes
     * @return FileVisitResult
     * @throws IOException
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        File file = path.toFile();
        //if this is not a java file, we are not interested
        //TODO implement support for other languages, ie Kotlin
        if (!file.getCanonicalPath().endsWith(".java")) {
            return FileVisitResult.CONTINUE;
        }

        //Tokenize file, and check do we have 4 expected strings in the row
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(new FileInputStream(file))));
            int currentToken = tokenizer.nextToken();
            int match = 0;
            while (currentToken != StreamTokenizer.TT_EOF) {

                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    if (expected.contains(tokenizer.sval)) {
                        match++;
                    } else {
                        match = 0;
                    }

                }
                //if we have, then we have candidate
                if (match == 4) {
                    paths.add(file.getCanonicalPath());
                }
                currentToken = tokenizer.nextToken();
            }

        } catch (IOException e) {
            //swallow exception, nobody cant see what we are doing here

        }
        return FileVisitResult.CONTINUE;
    }


}
