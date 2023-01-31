package fr.mireole.emotions.api.skin;

import com.mojang.blaze3d.systems.RenderSystem;
import fr.mireole.emotions.Emotions;
import fr.mireole.emotions.api.texture.SkinTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;

public class Skin {
    private AbstractTexture texture;
    private boolean slim;
    private final ResourceLocation location;
    private final String name;
    private boolean registered;
    private ResourceLocation skinLocation;

    public Skin(SkinTexture texture, boolean slim) {
        this.texture = texture;
        this.slim = slim;
        String path = texture.getSkinLocation().getPath();
        this.name = path.substring(path.lastIndexOf("/") + 1);
        this.location = new ResourceLocation(Emotions.MOD_ID, name + (slim ? "_slim" : "_normal"));
        this.skinLocation = texture.getSkinLocation();
    }

    public Skin(ResourceLocation location, boolean slim) {
        this.texture = Minecraft.getInstance().getTextureManager().getTexture(location);
        registered = true;
        this.slim = slim;
        String path = location.getPath();
        this.name = path.substring(path.lastIndexOf("/") + 1);
        this.location = location;
        this.skinLocation = null;
    }

    public void register() {
        if (!registered) {
            Minecraft.getInstance().getTextureManager().register(location, texture);
            registered = true;
        }
    }

    public void release() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> Minecraft.getInstance().getTextureManager().release(location));
        } else {
            Minecraft.getInstance().getTextureManager().release(location);
        }
        registered = false;
    }

    public boolean isSlim() {
        return slim;
    }

    public String getName() {
        return name;
    }

    public void setTexture(SkinTexture texture) {
        this.texture = texture;
    }

    public void setSlim(boolean slim) {
        this.slim = slim;
        if (skinLocation != null) {
            this.skinLocation = new ResourceLocation(skinLocation.getNamespace(), skinLocation.getPath().replace(slim ? "normal" : "slim", slim ? "slim" : "normal"));
        }
    }

    public AbstractTexture getTexture() {
        return texture;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public boolean isRegistered() {
        return registered;
    }

    public ResourceLocation getSkinLocation() {
        return skinLocation;
    }
}
