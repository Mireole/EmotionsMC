package fr.mireole.emotions.api.skin;

import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.texture.SkinTexture;
import fr.mireole.emotions.client.screen.EmotionsMainScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class SkinManager {
    private static final ArrayList<Skin> SKINS = new ArrayList<>();
    public static final Path SKINS_PATH = Minecraft.getInstance().gameDirectory.toPath().resolve("skins");
    public static final Path NORMAL_SKINS_PATH = SKINS_PATH.resolve("normal");
    public static final Path SLIM_SKINS_PATH = SKINS_PATH.resolve("slim");


    public static Skin createSkin(Path skinFile, boolean slim) {
        SkinTexture texture = createSkinTexture(skinFile);
        Skin skin = new Skin(texture, slim);
        skin.register();
        return skin;
    }

    public static Skin createSkin(ResourceLocation location, boolean slim) {
        return createSkin(location, slim, false);
    }

    public static Skin createSkin(ResourceLocation location, boolean slim, boolean registered) {
        Skin skin;
        if (registered) {
            skin = new Skin(location, slim);
        }
        else {
            SkinTexture texture = new SkinTexture(location);
            skin = new Skin(texture, slim);
            SKINS.add(skin);
        }
        return skin;
    }

    public static List<Skin> getSkins() {
        return SKINS;
    }

    public static Skin getSkin(String name) {
        for (Skin skin : SKINS) {
            if (skin.getName().equals(name)) {
                return skin;
            }
        }
        return null;
    }

    /**
     * Loads skins from the skins folder.
     */
    public static void loadSkins() {
        createDirectories();

        // Moving skins that are in skins to skins/normal
        getSkinTexturePaths(SKINS_PATH).forEach(path -> {
            Skin skin = createSkin(path, false);
            setSlim(skin, false);
        });

        // Load normal (skins/normal) and slim skins (skins/slim)
        getSkinTexturePaths(NORMAL_SKINS_PATH).forEach(path -> SKINS.add(createSkin(path, false)));
        getSkinTexturePaths(SLIM_SKINS_PATH).forEach(path -> SKINS.add(createSkin(path, true)));
    }

    private static List<Path> getSkinTexturePaths(Path folder) {
        List<Path> paths = new ArrayList<>();
        try (Stream<Path> list = Files.list(folder)) {
            list.forEach(path -> {
                if (path.getFileName().toString().endsWith(".png")) {
                    paths.add(path);
                }
            });
        } catch (IOException e) {
            Emotions.LOGGER.error("Could not list files in folder " + folder);
        }
        return paths;
    }

    public static void setSlim(Skin skin, boolean slim) {
        if (skin.getTexture() instanceof SkinTexture) {
            boolean registered = skin.isRegistered();
            skin.release();
            assert skin.getTexture() instanceof SkinTexture;
            Path texturePath = Path.of(((SkinTexture) skin.getTexture()).getSkinLocation().getPath());
            try {
                Path path;
                if (slim) {
                    path = SLIM_SKINS_PATH.resolve(texturePath.getFileName()).normalize();
                } else {
                    path = NORMAL_SKINS_PATH.resolve(texturePath.getFileName()).normalize();
                }
                Files.move(texturePath, path);
                skin.setTexture(createSkinTexture(path));
                skin.setSlim(slim);
                if(registered) {
                    skin.register();
                }
            } catch (IOException e) {
                Emotions.LOGGER.error("Could not move skin " + texturePath + " to " + (slim ? SLIM_SKINS_PATH : NORMAL_SKINS_PATH));
            }
            if(Minecraft.getInstance().screen instanceof EmotionsMainScreen screen) {
                screen.generatePlayers();
            }
        }
    }

    public static void createDirectories(){
        try {
            if (!Files.exists(SKINS_PATH)) {
                Files.createDirectory(SKINS_PATH);
            }
            if(!Files.exists(SLIM_SKINS_PATH)){
                Files.createDirectory(SLIM_SKINS_PATH);
            }
            if(!Files.exists(NORMAL_SKINS_PATH)){
                Files.createDirectory(NORMAL_SKINS_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SkinTexture createSkinTexture(Path skinFile) {
        return new SkinTexture(new ResourceLocation(Emotions.MOD_ID, Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath().relativize(skinFile.toAbsolutePath()).toString().replace("\\", "/")));
    }

}
