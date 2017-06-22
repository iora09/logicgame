package com.bob.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelFactory {
    public static final List<Level> WRITE = new ArrayList<>();
    public static final List<Level> READ = new ArrayList<>();
    public static final List<Level> MACRO = new ArrayList<>();
    public static final List<Level> TUTORIAL_CONTROLS = new ArrayList<>();
    public static final List<Level> TUTORIAL_NOT = new ArrayList<>();
    public static final List<Level> TUTORIAL_AND = new ArrayList<>();


    public static void initialiseLevels() {
        populateWrite();
        populateRead();
        populateMacro();
        populateTutorials();
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

    private static void populateTutorials() {
        File folder = new File("levels/tutorials");
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

        return loadLevelFromXmlRoot(root, file.nameWithoutExtension());
    }

    private static Level loadLevelFromXmlRoot(XmlReader.Element root, String name) {
        String type = root.getAttribute("type");

        switch (type) {
            case "WRITE": {
                int index = getReplaceIndex(WRITE, name);
                if (index >= 0) {
                    WRITE.set(index, new WriteLevel(root, name));

                } else {
                    WRITE.add(new WriteLevel(root, name));
                    index = WRITE.size() - 1;
                }
                setNextInList(WRITE, index);
                return WRITE.get(index);

            }
            case "READ": {
                int index = getReplaceIndex(READ, name);
                if (index >= 0) {
                    READ.set(index, new ReadLevel(root, name));

                } else {
                    READ.add(new ReadLevel(root, name));
                    index = READ.size() - 1;
                }
                setNextInList(READ, index);
                return READ.get(index);
            }

            case "MACRO": {
                int index = getReplaceIndex(MACRO, name);
                if (index >= 0) {
                    MACRO.set(index, new MacroLevel(root, name));

                } else {
                    MACRO.add(new MacroLevel(root, name));
                    index = MACRO.size() - 1;
                }
                setNextInList(MACRO, index);
                return MACRO.get(index);
            }

            case "TUTORIAL": {
                String tutType = root.getAttribute("tut_type");
                switch (tutType) {
                    case "CONTROLS" : return getTutorial(TUTORIAL_CONTROLS, name, root);
                    case "NOT" : return getTutorial(TUTORIAL_NOT, name, root);
                    case "AND" : return getTutorial(TUTORIAL_AND, name, root);
                    default: return null;
                }

            }

            case "CREATE": {
                return new WriteLevel(root, "empty");
            }
            default: return null;
        }
    }

    private static Level getTutorial(List<Level> tutorials, String name, XmlReader.Element root) {
        int index = getReplaceIndex(tutorials, name);
        if (index >= 0) {
            tutorials.set(index, new TutorialLevel(root, name));

        } else {
            tutorials.add(new TutorialLevel(root, name));
            index = tutorials.size() - 1;
        }
        setNextInList(tutorials, index);
        return tutorials.get(index);
    }

    public static Level createLevel(String type, XmlReader.Element root, String name) {
        switch (type) {
            case "READ" : return new ReadLevel(root, name);
            case "WRITE" : return new WriteLevel(root, name);
            case "MACRO" : return new MacroLevel(root, name);
            case "TUTORIAL" : return new TutorialLevel(root, name);
            default: return null;
        }
    }

    public static Level createLevel(String gameXml, String gameName) {
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element root = xmlReader.parse(gameXml);
        return createLevel(root.getAttribute("type"), root, gameName);
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
