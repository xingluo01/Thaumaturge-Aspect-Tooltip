package io.github.xingluo.thaumaturgeaspecttooltip.client;

import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectIndexAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectKnowledgeAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import io.github.xingluo.thaumaturgeaspecttooltip.ThaumaturgeAspectTooltip;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * 给「有要素数据、但本体没给文本行」的物品补一行文本要素。
 *
 * <p>本体 {@code AspectContainerTooltipHandler} 只对两类物品输出文本：实现 {@code IItemEssentia}
 * 的源质容器（魔力安瓿等）与实现 {@code ILabel} 的物品（魔力水晶碎片、魔豆等）。
 * 其余物品只有 Shift 图形 Chip。本类用本体同一个要素索引 {@link AspectIndexAccess} 取数据，
 * 对剩余物品补齐同样的文本，格式、颜色、知识门槛全部照抄本体，玩家看到的是一套东西。
 *
 * <p>三条边界（见 docs/ARCHITECTURE.md）：
 * <ol>
 *   <li>tooltip 里已有要素表头（不管谁加的）→ 整条跳过，不重复、不重排。</li>
 *   <li>不监听 Shift、不渲染图标、不碰 Chip。</li>
 *   <li>纯客户端，只在 {@link ItemTooltipEvent} 里改文本。</li>
 * </ol>
 */
@EventBusSubscriber(modid = ThaumaturgeAspectTooltip.MOD_ID, value = Dist.CLIENT)
public final class AspectTooltipHandler {
    /** 表头颜色，与本体 {@code AspectContainerTooltipHandler} 的 11902681（#B59ED9）一致。 */
    private static final int HEADER_COLOR = 0xB59ED9;

    /**
     * 要素行的缩进：本体用 {@code Component.literal("  ")} 起头，两个半角空格。
     * 少了它，要素行会顶到表头左边，和本体那几类物品放一起看就不齐。
     */
    private static final String ASPECT_INDENT = "  ";

    /** 本体的文案键，直接复用：两种语言都已有译文，措辞与本体完全一致。 */
    private static final String HEADER_KEY = "tooltip.thaumaturge.aspects.header";
    private static final String AMOUNT_KEY = "tooltip.thaumaturge.amount";

    /**
     * {@code AspectIndexAccess.index()} 在绑定前会抛 {@link IllegalStateException}。
     * 正常进世界后不会发生；一旦发生就永久关闭注入，避免每次悬停都构造异常。
     */
    private static boolean indexUnavailable;

    private AspectTooltipHandler() {}

    /**
     * 用 LOWEST：本体与其它 mod 都是默认 NORMAL，先让它们写完，再判断有没有表头。
     * 若本类先跑，本体的表头会随后被追加，出现两份要素行。
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        // 与本体一致：没有打开的界面时 tooltip 不会真正渲染，此时不写入。
        if (Minecraft.getInstance().screen == null) {
            return;
        }

        List<Component> lines = event.getToolTip();
        if (hasAspectHeader(lines)) {
            return;
        }
        if (indexUnavailable) {
            return;
        }

        AspectList aspects;
        try {
            aspects = AspectIndexAccess.of(stack);
        } catch (IllegalStateException notBoundYet) {
            indexUnavailable = true;
            return;
        }
        if (aspects == null || aspects.isEmpty()) {
            return;
        }

        // 与本体相同的位置：紧贴物品名，数量多的排前面。
        int at = Math.min(1, lines.size());
        lines.add(at++, Component.translatable(HEADER_KEY)
                .withStyle(style -> style.withColor(TextColor.fromRgb(HEADER_COLOR))));
        for (AspectInstance instance : aspects.sortedByAmount().reversed()) {
            lines.add(at++, aspectLine(instance));
        }
    }

    /**
     * 单条要素行。已知的要素带数量，未发现的只显示名字——这一点必须跟本体一致，
     * 否则会把玩家还没研究出来的要素数量提前暴露。
     */
    private static Component aspectLine(AspectInstance instance) {
        Holder<IAspect> aspect = instance.aspect();
        MutableComponent text = AspectKnowledgeAccess.isKnown(aspect)
                ? Component.translatable(AMOUNT_KEY, AspectComponents.name(aspect), instance.amount())
                : AspectComponents.name(aspect);
        return Component.literal(ASPECT_INDENT)
                .append(text)
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(aspect.value().color())));
    }

    /**
     * 判定口径：tooltip 里是否已有要素表头。
     *
     * <p>按翻译键比对而不是按渲染后的字符串，切语言、装汉化包都不影响判断；
     * 递归看兄弟节点，因为别的 mod 可能把表头拼进一个复合组件里。
     */
    private static boolean hasAspectHeader(List<Component> lines) {
        for (Component line : lines) {
            if (containsKey(line, HEADER_KEY)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsKey(Component component, String key) {
        if (component.getContents() instanceof TranslatableContents translatable
                && translatable.getKey().equals(key)) {
            return true;
        }
        for (Component sibling : component.getSiblings()) {
            if (containsKey(sibling, key)) {
                return true;
            }
        }
        return false;
    }
}
