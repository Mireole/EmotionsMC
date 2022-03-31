package fr.mireole.emotions.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class SkinTexture extends AbstractTexture {
    private final ResourceLocation skinLocation;

    public SkinTexture(ResourceLocation skinLocation) {
        this.skinLocation = skinLocation;
    }

    @Override
    public void load(@NotNull ResourceManager p_117955_) {
        SkinImage skinImage = SkinImage.load(skinLocation);
        if(skinImage != null) {
            NativeImage nativeimage = skinImage.getImage();
            if (!RenderSystem.isOnRenderThreadOrInit()) {
                RenderSystem.recordRenderCall(() -> loadImage(nativeimage));
            } else {
                loadImage(nativeimage);
            }
        }

    }

    public void loadImage(NativeImage image){
        TextureUtil.prepareImage(this.getId(), 0, image.getWidth(), image.getHeight());
        image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), false, false, false, true);
    }

    public ResourceLocation getSkinLocation() {
        return skinLocation;
    }

    protected record SkinImage(NativeImage image) implements Closeable {

        public static SkinImage load(ResourceLocation skinLocation) {
            try {
                FileInputStream stream = new FileInputStream(skinLocation.getPath());
                NativeImage image = NativeImage.read(stream);
                return new SkinImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void close() {
            this.image.close();
        }

        public NativeImage getImage() {
            return image;
        }
    }

}
