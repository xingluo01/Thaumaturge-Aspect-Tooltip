package io.github.xingluo.thaumaturgeaspecttooltip.client;

import io.github.xingluo.thaumaturgeaspecttooltip.ThaumaturgeAspectTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * 客户端 tooltip 注入骨架。
 *
 * <p>当前为空实现，只保证工程可编译、dev client 能加载。
 * 实际逻辑见 docs/ARCHITECTURE.md 与 docs/TODO.md：
 * <ol>
 *   <li>用 javap 核对 Thaumaturge {@code api.aspect} 包里按 ItemStack 取要素的方法签名。</li>
 *   <li>读要素列表；若原生 tooltip 已有文本要素行则跳过（判定口径见架构文档待定项）。</li>
 *   <li>无则向 {@code event.getToolTip()} 追加一行文本组件，文案走本地化 key。</li>
 * </ol>
 * Shift 触发的图形 Chip 版属于本体，本类不监听 Shift、不渲染图标。
 */
@EventBusSubscriber(modid = ThaumaturgeAspectTooltip.MOD_ID, value = Dist.CLIENT)
public final class AspectTooltipHandler {
    private AspectTooltipHandler() {}

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        // 雏形阶段：不改动任何 tooltip。
        // 实现时在此读 Thaumaturge 要素 API 并按需追加文本行。
    }
}
