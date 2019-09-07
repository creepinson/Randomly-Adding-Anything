package fr.arthurbambou.randomlyaddinganything.client;

import fr.arthurbambou.randomlyaddinganything.materials.Material;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Random;
import java.util.function.Supplier;

import static net.minecraft.block.BlockRenderLayer.CUTOUT;

public class OreBakedModel extends RAABakedModel {

    public OreBakedModel(Material material) {
        super(material);
    }

    @Override
    public void emitBlockQuads(ExtendedBlockView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        context.meshConsumer().accept(mesh());
    }

    private Mesh mesh() {
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        RenderMaterial mat = renderer.materialFinder().disableDiffuse(0, true).find();
        int color = 0xFFFFFFFF;
        Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas().getSprite(new Identifier("block/" + Registry.BLOCK.getId(material.getOreInformation()
                .getGenerateIn().getBlock()).getPath()));
        
        emitter.square(Direction.SOUTH, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.EAST, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.WEST, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();

        if (material.isGlowing()) {
            mat = renderer.materialFinder().disableDiffuse(0, true).blendMode(0, CUTOUT).emissive(0, true).find();
        } else {
            mat = renderer.materialFinder().disableDiffuse(0, true).blendMode(0, CUTOUT).find();
        }
        color = material.getRGB();
        sprite = MinecraftClient.getInstance().getSpriteAtlas().getSprite(this.material.getOreInformation().getOverlayTexture());

        emitter.square(Direction.SOUTH, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.EAST, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.WEST, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.NORTH, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.DOWN, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();
        emitter.square(Direction.UP, 0, 0, 1, 1, 0)
                .material(mat)
                .spriteColor(0, color, color, color, color)
                .spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV).emit();

        return builder.build();
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        context.meshConsumer().accept(mesh());
    }

    protected class ItemProxy extends ModelItemPropertyOverrideList {
        public ItemProxy() {
            super(null, null, null, Collections.emptyList());
        }

        @Override
        public BakedModel apply(BakedModel bakedModel_1, ItemStack itemStack_1, World world_1, LivingEntity livingEntity_1) {
            return OreBakedModel.this;
        }
    }

    @Override
    public ModelItemPropertyOverrideList getItemPropertyOverrides() {
        return new ItemProxy();
    }
}