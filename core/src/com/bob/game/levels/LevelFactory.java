package com.bob.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelFactory {
    public static final List<Level> WRITE = new ArrayList<>();
    public static final List<Level> READ = new ArrayList<>();
    public static final List<Level> MACRO = new ArrayList<>();

    public static void initialiseLevels() {
        populateWrite();
        populateRead();
        populateMacro();
    }

    private static void populateWrite() {
        File folder = new File("levels/write");
        for (final File file : folder.listFiles()) {
            loadInternalLevel(file.getPath());
        }
    }

    private static void populateRead() {
        File folder = new File("levels/read");
        for (final File file : folder.listFiles()) {
            loadInternalLevel(file.getPath());
        }
    }

    private static void populateMacro() {
        File folder = new File("levels/macro");
        for (final File file : folder.listFiles()) {
            loadInternalLevel(file.getPath());
        }
    }

    public static Level loadExternaLevel(String path) {
        return(loadLevelFromFile(new FileHandle(path)));
    }

    public static Level loadInternalLevel(String path) {
        return(loadLevelFromFile(Gdx.files.internal(path)));
    }

    public static Level loadLevelFromFile(FileHandle file) {
        XmlReader xmlReader = new XmlReader();

        XmlReader.Element root = null;

        try {
            root = xmlReader.parse(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String type = root.getAttribute("type");

        switch (type) {
            case "WRITE": {
                int index = getReplaceIndex(WRITE, file.nameWithoutExtension());
                if (index >= 0) {
                    WRITE.set(index, new WriteLevel(root, file.nameWithoutExtension()));
                    
                } else {
                    WRITE.add(new WriteLevel(root, file.nameWithoutExtension()));
                    index = WRITE.size() - 1;
                    //file.copyTo(new FileHandle("levels/write"));
                }
                setNextInList(WRITE, index);
                return WRITE.get(index);
                
            }
            case "READ": {
                int index = getReplaceIndex(READ, file.nameWithoutExtension());
                if (index >= 0) {
                    READ.set(index, new ReadLevel(root, file.nameWithoutExtension()));

                } else {
                    READ.add(new ReadLevel(root, file.nameWithoutExtension()));
                    index = READ.size() - 1;
                    //file.copyTo(new FileHandle("levels/read"));
                }
                setNextInList(READ, index);
                return READ.get(index);
            }

            case "MACRO": {
                int index = getReplaceIndex(MACRO, file.nameWithoutExtension());
                if (index >= 0) {
                    MACRO.set(index, new MacroLevel(root, file.nameWithoutExtension()));

                } else {
                    MACRO.add(new MacroLevel(root, file.nameWithoutExtension()));
                    index = MACRO.size() - 1;
                    //file.copyTo(new FileHandle("levels/macro"));
                }
                setNextInList(MACRO, index);
                return MACRO.get(index);
            }
            default: return null;
        }

    }

    private static void setNextInList(List<Level> levels, int nextIndex) {
        if (nextIndex > 0) {
            levels.get(nextIndex - 1).setNext(levels.get(nextIndex));
        }
    }

    private static int getReplaceIndex(List<Level> levels, String levelName) {
        for(int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getLevelName().equals(levelName)) return i;
        }
        return -1;
    }

}
