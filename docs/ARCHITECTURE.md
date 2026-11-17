# 架构约定

改任何代码前先读本页。

## 源集

| 源集 | 放什么 | 依赖 |
|---|---|---|
| `src/main/java` | 公共标记、常量；纯客户端功能下保持最小 | main |
| `src/client/java` | tooltip 注入、渲染相关全部代码 | main.output + main.compileClasspath |
| `src/test/java` | NeoForge GameTest / 单测 | main.output |
| `src/main/templates` | `neoforge.mods.toml` 模板，经 `generateModMetadata` 展开 | — |

客户端边界：`src/main` 不得 import `net.minecraft.client.*` 或本模组的 client 包。
`jar` 任务已把 `sourceSets.client.output` 合并进产物，remapJar 亦依赖它。

## 功能边界

只做一件事：**物品有要素数据、却没有原生文本要素行时，在 tooltip 追加一行文本。**

三条硬约束：

1. 原生已有文本要素行 → 跳过。不重复、不覆盖、不重排原生 tooltip。
2. Shift 触发的图形 Chip 版属于本体，本模组不监听 Shift、不渲染图标、不改 Chip。
3. 服务端不注册任何内容。注入只发生在客户端 tooltip 事件。

本体目前的文本覆盖只有两类物品：实现 `IItemEssentia` 的源质容器（魔力安瓿等）与实现
`ILabel` 的物品（魔力水晶碎片、魔豆等）。其余物品只有 Shift 图形 Chip，正是本模组要补的范围。

## 要素数据来源

只经 Thaumaturge 的公开 API 读，包为 `com.leclowndu93150.thaumaturge.api.aspect`。
以下签名已用 `javap` 对 `libs/thaumaturge-1.21.1-NeoForge-1.1.0.jar` 核对过，改版本要重核：

| 用途 | 签名 |
|---|---|
| 取物品要素 | `AspectIndexAccess.of(ItemStack) → AspectList`（另有 `of(Item)`） |
| 要素列表 | `AspectList.isEmpty()`、`size()`、`sortedByAmount() → List<AspectInstance>` |
| 单条要素 | `AspectInstance.aspect() → Holder<IAspect>`、`amount() → int` |
| 名称 | `AspectComponents.name(Holder<IAspect>) → MutableComponent`（另有 `shortName`/`trueName`/`description`/`help`/`composition`） |
| 颜色 | `IAspect.color() → int`（RGB） |
| 知识门槛 | `AspectKnowledgeAccess.isKnown(Holder<IAspect>) → boolean` |
| 图形 Chip | `AspectChipsTooltip(AspectList)`，由本体 `AspectTooltipEvents` 在 Shift 时挂载 |

不反射私有字段、不复制要素表、不硬编码要素 id。

两个易踩的点：

- `AspectIndexAccess.index()` 在绑定前抛 `IllegalStateException`（`bind` 未调用时），
  取数据要 try/catch 并做一次性降级，不要让它冒到 tooltip 渲染里。
- `AspectKnowledgeAccess` 自带默认 resolver（返回 `AspectKnowledge.KNOWN`），未绑定时不抛异常，
  但**仍然要用它**：玩家没研究出来的要素，本体只显示名字不显示数量。

## 判定口径（已定）

「原生已有文本行」= **tooltip 里存在要素表头**，按翻译键 `tooltip.thaumaturge.aspects.header`
比对，递归看兄弟节点。理由：

- 按翻译键而不是渲染后的字符串，切语言、装汉化包都不影响判定。
- 本体只对两类物品输出文本（`IItemEssentia` 源质容器、`ILabel` 物品），它们都会带这行表头；
  其它 mod 若也照这个格式写，同样能被识别，不会叠两份。
- 比「按物品类型判断」稳：以后本体扩到别的物品，本模组自动让位。

## 实现要点

- 监听 `ItemTooltipEvent`，优先级 `LOWEST`：本体与其它 mod 都是默认 NORMAL，先让它们写完
  再判断，否则本类先插入、本体随后再插一份，会出现两组要素行。
- 插入位置与本体一致：表头插在索引 1（紧贴物品名），要素行依次排在后面，数量多的在前。
- 颜色照抄本体：表头 `0xB59A6A`，要素行用 `IAspect.color()`，整行同色。
- 文案直接复用本体键 `tooltip.thaumaturge.aspects.header`（「要素：」/「Aspects:」）与
  `tooltip.thaumaturge.amount`（`%s x%s`）。本体已带 zh_cn / en_us 译文，本模组不再自带语言文件，
  措辞与本体永远一致。
- 本体 `shouldShow()` 要求 `Minecraft.getInstance().screen != null`，本模组同样加这道判断。

## 依赖

- `thaumaturge`：required，版本下限 `gradle.properties` 的 `thaumaturge_version`，同步写进 mods.toml。
- 不兼容其他神秘重制核心（三花 / Thavma / Thaumon / Etherology）：
  它们的要素注册与 api 包不同，本模组只认 modid `thaumaturge`。

## 生成物

`neoforge.mods.toml` 从模板生成，不手改 `build/generated/` 下的东西。
`generateModMetadata` 把 `gradle.properties` 的字段展开进模板。
