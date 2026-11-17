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

## 要素数据来源

只经 Thaumaturge 的公开 API 读，包为 `com.leclowndu93150.thaumaturge.api.aspect`。
当前版本 jar 里与本需求相关的类（类名来自 jar 清单，方法签名需 javap 复核）：

- `Aspects`、`AspectList`、`IAspect`、`AspectInstance` — 要素与要素列表
- `AspectDataMaps` — 物品到要素的数据映射
- `AspectCapabilities`、`IAspectContainer` — 附着在物品/实体上的要素能力
- `AspectChipsTooltip` — 本体构建图形 Chip 提示的内部入口（只读参考，不改）

注入点用 NeoForge 的 `ItemTooltipEvent`（客户端），在追加阶段读要素列表。
不反射私有字段、不复制要素表、不硬编码要素 id。

## 待定项：怎样算「没有文本行」

实现前必须先定判定口径，两种备选：

1. **按原生 tooltip 内容判定**：扫描事件里已有的文本组件，找「要素」标签行；
   有则跳过，无则追加。优点是贴合玩家所见，缺点是依赖本体的文案格式，版本升级易碎。
2. **按本体是否登记显示策略判定**：查本体有没有把该物品标成「已显示文本」；
   有 API 则最稳，没有则退回方案 1。

推荐先查 API 是否存在方案 2 的开关；若不存在，用方案 1 并把标签文案做成可配置项。

## 依赖

- `thaumaturge`：required，版本下限 `gradle.properties` 的 `thaumaturge_version`，同步写进 mods.toml。
- 不兼容其他神秘重制核心（三花 / Thavma / Thaumon / Etherology）：
  它们的要素注册与 api 包不同，本模组只认 modid `thaumaturge`。

## 生成物

`neoforge.mods.toml` 从模板生成，不手改 `build/generated/` 下的东西。
`generateModMetadata` 把 `gradle.properties` 的字段展开进模板。
