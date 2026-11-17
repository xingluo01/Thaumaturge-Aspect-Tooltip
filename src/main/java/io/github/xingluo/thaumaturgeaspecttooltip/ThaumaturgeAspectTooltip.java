package io.github.xingluo.thaumaturgeaspecttooltip;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/**
 * Thaumaturge Aspect Tooltip 的公共入口。
 *
 * <p>本模组是纯客户端功能：tooltip 注入全部位于 client 源集，
 * 这里只保留一个空的 mod 容器，供 NeoForge 识别 modId。
 * 不要在 main 源集引用任何客户端类或 Thaumaturge 的 client API。
 */
@Mod(ThaumaturgeAspectTooltip.MOD_ID)
public final class ThaumaturgeAspectTooltip {
    public static final String MOD_ID = "thaumaturge_aspect_tooltip";

    public ThaumaturgeAspectTooltip(IEventBus modBus) {
        // 公共总线上暂无订阅。若将来需要在客户端注册事件，
        // 走 client 源集的构造入口，不在这里直接引用 net.minecraft.client.*。
    }
}
