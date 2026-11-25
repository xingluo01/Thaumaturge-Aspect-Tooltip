# 开发环境

## 前置

- JDK 21（toolchain 固定），可选 JDK 25 供 Gradle 自身使用。
  `gradle.properties` 的 `org.gradle.java.installations.paths` 指向实际安装路径。
- Thaumaturge 本体 1.1.0 已通过 CurseMaven 坐标 `curse.maven:thaumaturge-1628024:9452543` 声明，构建与 dev 运行期自动下载，无需手工放进 `libs/`。
- Gradle 9.5 由 wrapper 自动下载；首次构建需联网。

## 开放环境 mod（dev runtime）

dev client 加载的 mod 全部走 `localRuntime`，不进成品 jar、不进 git。
**硬规则：有官方 Maven / CurseMaven / Modrinth 源的依赖必须用坐标声明，禁止把 jar 放进
`libs/devmods-local/` 或从整合包拷贝。** 只有确认无可用官方坐标时才允许本地 jar，
且必须在 `docs/LOCAL_DEPS.md` 登记版本、来源路径与哈希。

### 前置（必须）

Thaumaturge 1.1.0 是 required 前置，缺它 tooltip 注入拿不到要素数据、启动也会失败。
已确认官方 CurseForge 项目（ID `1628024` / File `9452543`，`thaumaturge-1.21.1-NeoForge-1.1.0.jar`）
通过 CurseMaven 可达，直接用坐标同时做编译期与运行期依赖：

| 配置 | 作用 |
|---|---|
| `compileOnly 'curse.maven:thaumaturge-1628024:9452543'` | main 源集编译期可见要素 API |
| `clientCompileOnly 'curse.maven:thaumaturge-1628024:9452543'` | client 源集编译期可见 |
| `localRuntime 'curse.maven:thaumaturge-1628024:9452543'` | dev client / server / data 运行期真正加载 |

Thaumaturge 与 ExtendedAE 在 dev 环境还需要两个运行期前置，均有官方 Modrinth Maven 源，
必须用坐标声明，不得从整合包拷 jar：

| 前置 | 坐标 | 项目 ID / 版本 ID | 来源要求 |
|---|---|---|---|
| glodium | `maven.modrinth:glodium:1.21-2.2-neoforge` | `UhW5uCKw` / `pfbmdJ3b` | extendedae 要求 `>= 1.21-2.0-neoforge` |
| lithostitched | `maven.modrinth:lithostitched:1.8.1-neoforge-21.1` | `XaDC71GB` / `cZu4cHY3` | thaumaturge 要求 `>= 1.8.0` |

坐标解析出的 jar 与整合包文件 SHA-1 一致：glodium `9f61a3162665ad0b37f6f268339ac98097af1a87`，
lithostitched `88cdded5b617e19b4d2676948b5550699de23578`。

### 测试套件（dev-only）

**以 Maven 坐标声明为主**（`build.gradle` 的 `localRuntime '...'`），不用往仓库塞 jar。
Coordinates 优先 CurseMaven，其余走 Modrinth（通过 `api.modrinth.com/maven`）、
BlameJared（JEI）、Latvian（KubeJS/Rhino）。版本与整合包一致，测试才反映玩家真实环境。
少数没有可用公开坐标的 mod 才放本地 jar 到 `libs/devmods-local/`（目录 9 个，当前启用 4 个）。
名册按用途简单分组（版本以 `build.gradle` 为准；运行时另带入少量传递依赖）：

| 分组 | modid（版本） | 用途 |
|---|---|---|
| 本体与必需前置 | `thaumaturge` 1.1.0、`glodium` 1.21-2.2、`lithostitched` 1.8.1、`guideme` 21.1.22、`mezz_config` 0.7.1-standalone、`mezz_config_gui` 0.5.5、`ae2wtlib_api` 19.5.1 | 本模组前置与运行期硬依赖，均走 Maven 坐标 |
| 公共库与脚本 | `architectury` 13.0.11、`cloth_config` 15.0.140、`curios` 9.5.1、`geckolib` 4.9.3、`ftblibrary` 2101.1.39、`modernui` 3.13.0.1、`kubejs` 2101.7.2、`rhino` 2101.2.7 | 公共库、UI 底座、脚本环境，可动态改 tooltip 做对照 |
| 渲染、性能与诊断 | `sodium` 0.6.13、`fusion` 1.3.16、`modernfix` 5.27.27、`spark` 1.10.124、`mod_menu` 1.0.2 | 验证优化环境下的 tooltip，并用 spark 定位卡顿 |
| Tooltip / JEI 验证 | `jade` 15.10.6、`jadeaddons` 6.1.2、`jei` 19.70.0.469、`jecharacters` 4.5.30、`itemzoomer` 2.0.1、`polymorph` 1.1.0 | tooltip 增强、文本叠加、拼音搜索与多结果提示的主要对照目标 |
| AE2 样板与存储 | `ae2` 19.2.19、`ae2jeiintegration` 1.2.1、`ae2wtlib` 19.5.1、`extendedae` 2.2.43、`ae2omnicells` 1.1.6、`ae2_pattern_disk` 0.9.13 | 大量带要素物品样本、样板/磁盘/无线终端场景 |
| 神秘使附属（保留 3 个） | `thaumicenergistics_ce` 2.7.5.4、`thaumicbases` 3.6.023.5、`thaumaturge_tinkerer` 0.3 | 源质接入 AE2、泛用内容扩展、Tinkerer 移植；其余附属已禁用 |

`ae2_pattern_disk` 为整合包外补入的稳定版，版本取参考仓库 AE2-Pattern-Disk 验证过的值。
闪电科技三件（`ae2lt`、`ae2ltpp`、`thunderbolt`）已从 dev 环境移除，以排查主菜单卡顿；
需要时再单独加回。

坐标来源：主流 mod 由参考仓库 AE2-Pattern-Disk / TE4 验证过的 curse.maven 坐标声明；
其余用 jar 的 SHA-1 在 Modrinth API 反查出精确版本坐标。
`libs/devmods-local/` 目录仍有 9 个 jar，当前只启用 4 个：`ExtendedAE`、`ae2jeiintegration`、
`thaumaturge_tinkerer`、`thaumicbases`。其余 5 个神秘使附属 jar 保留文件但不加载：
`automagia`、`classic_thaumonomicon`、`kubethaum`、`thaumaturgeadditions`、`thaumictinkerer`。
`thaumadditions` 与 `thaumictracker` 的 Maven 坐标也已从 `build.gradle` 移除。
本机路径见不进 git 的 `docs/LOCAL_DEPS.md`。
这些依赖不在 `sources` 里，不影响编译，只影响 `runClient` 的加载列表。

### 主菜单卡顿排查线索（2026-11-17）

`run/logs/latest.log` 显示两段 16 秒级停顿：资源重载/纹理图集烘焙（约 16.6s，到
`signs.png-atlas`）与进入主菜单后 ModernUI 文本布局引擎 `res level: 2 to 4`（约 16.2s，
Render thread）。后者与“进主菜单后严重卡顿”的时间点吻合。

首要嫌疑是 ModernUI 的字体/emoji 栈叠加当时启用的两个汉化资源包：日志扫描 6923 个 emoji shortcode、
3581 个 emoji map，并加载 Source Han Sans CN Medium 等 CJK 字体。GuideME / `ae2_pattern_disk` 的
指南页索引冲突与图标缺失集中在 13:57:33，是次级嫌疑。
本模组 `thaumaturge_aspect_tooltip` 没有对应异常。

要做定性结论，应在卡顿时执行 `/spark profiler` 采样。当前已停用通用汉化包，仅保留专项翻译包
文件夹版，并在 `run/options.txt` 固定 `guiScale:3`，避免自动缩放触发 `res level: 2 to 4`。
若主菜单仍卡，再把 `resourcePacks` 置空验证专项包，随后 A/B GuideME / `ae2_pattern_disk`、
KubeJS / Rhino。ModernUI 自身也已按“只保留三项能力”收敛（见下方 ModernUI 配置一节），
复测时可把它与资源包两个变量分开看。

**2026-11-17 15:15 复测结果**：`res level` 全程停在 `2 to 2`（退出世界回标题时一次 `2 to 3`），
`2 to 4` 未再出现——guiScale 推断成立。但资源重载仍要 33–41s，大头是纹理图集烘焙
（blocks → signs 9–12s，armor_trims → decorated_pot 6–12s）；这台机器是 Intel HD Graphics
Gen11 核显，属固有开销，与字体等级无关。

**结论（2026-11-17 15:35）**：卡顿主因是渲染路径，既不是资源包也不是本模组。
上一轮 Sodium 因 JarJar 壳问题实际没生效（见「JarJar wrapper 陷阱」），客户端跑的是
纯原版渲染器 + 45 个 mod 的渲染钩子。换 0.6.13 生效后主菜单卡顿消失，实测：

| 阶段 | 无 Sodium | 有 Sodium |
|---|---|---|
| blocks → signs 图集 | 8.7s / 9.2s | 7.9s |
| armor_trims → decorated_pot | 5.7s / 12.2s | 0.56s |
| banner_patterns | 2.0s | 0.01s |
| 整轮资源重载 | 32.7s / 41.0s | 29.7s |

原因：主菜单不是静态图，而是大量 immediate-mode 四边形（全景、按钮、文字、tooltip）。
Sodium 注入了 `BufferBuilder` intrinsics、20 字节紧凑顶点格式、`TextureAtlas` 上传路径与
`BakedGlyph` 字形路径（见 `sodium-common.mixins.json`）。这台机器是 Intel HD Gen11 核显，
没有独立显存，原版路径把 CPU 拖在同一条内存总线上，帧时间尖峰就表现为卡顿。
`/spark profiler` 采样暂时不必做了，除非后续再出现卡顿。

**仓库配置**：`repositories` 段为这些坐标加了 cursemaven、api.modrinth.com/maven、
BlameJared、Latvian、JitPack、Shedaniel。本机 `maven.modrinth.com` 不通（TLS 连接被关闭），
故用 `api.modrinth.com/maven` 提供同一 `maven.modrinth` group，不要改回旧域名。

**Modrinth 坐标必须用 version ID**：`maven.modrinth:<artifact>:<version-id>`。同一 `version_number`
会同时挂在 NeoForge 与 Fabric 构件上，裸版本号可能解析到另一个 loader 的 jar。三个曾出错的坐标改为：

| mod | 错误写法（裸版本号） | 正确写法（version ID） |
|---|---|---|
| jecharacters | `maven.modrinth:justenoughcharacters:4.5.30` | `maven.modrinth:justenoughcharacters:iNnJuUnx` |
| mezz_config | `maven.modrinth:mezzconfig:0.7.1` | `maven.modrinth:mezzconfig:V6oWFnxW` |
| polymorph | `maven.modrinth:polymorph:1.1.0+1.21.1` | `maven.modrinth:polymorph:VEburL70` |

参考仓库 AE2-Pattern-Disk 的 `build.gradle:372-374` 明确记录了 JECharacters 的同类陷阱；
当前 `build.gradle` 也加了对应注释。

**JarJar wrapper 陷阱（Sodium 0.8.x）**：Modrinth Maven 上的
`sodium:mc1.21.1-0.8.13-neoforge` 只是个壳——外层仅 72 个预启动检查类加 `META-INF/jarjar/`
（内层真 mod + 4 个 FFAPI 模块），本身 **0 个 assets**。作为 classpath mod 传入时，JarJar 把
内层依赖解析成这个壳自己（日志：`passed in as source: sodium. Using Mod File:
...sodium-mc1.21.1-0.8.13-neoforge.jar`），真 jar 从不解包：Mod List 里有 Sodium，但没有
mixin、没有资源包、不生成 `sodium-options.json`，等于没装。

判据：一次资源重载的 51 个包里，只有 `minecraft` 和 `sodium` 没有对应的 `mod/<id>` 条目，
而 Sodium 真 jar 自带 80+ 个 assets 条目。改用自包含的 `mc1.21.1-0.6.13-neoforge`
（576 个类、80 个 assets、mixin 配置在 jar 根、同样要求 neoforge `[21.1.82,)`）即可。

**NeoForge 基线已升到 21.1.265**：`thaumicbases` 声明 `neoforge [21.1.265,)`，
低于此版本 FML 会拒绝加载。21.1.265 也是整合包实例实际使用的版本
（`axolotl_config.json` 的 `content_set.loader_version`），升基线使 dev 与玩家环境一致。

### 汉化资源包（dev client）

不走 `localRuntime`，放在 `run/resourcepacks/`，由 `run/options.txt` 的 `resourcePacks` 行启用。
当前只启用专项翻译包文件夹版；通用包 `Minecraft-Mod-Language-Modpack-Converted-1.21.1.zip`
留在目录中但不在启用列表，不参与加载。

| 包 | 作用 |
|---|---|
| `Extra Mods ZH_CH/` | 本机补充汉化（17 个命名空间），文件夹版 |

`run/` 被 gitignore，换机器需重新放置该文件夹与 options.txt，
步骤见 `docs/LOCAL_DEPS.md`。本仓库的要素文案本地化（zh_cn/en_us）仍需按 `docs/TODO.md` 实现。

**配置坑（已修，勿回退）**：MDG 的 `runClient` 走 `clientRuntimeClasspath`，它不会自动继承
`localRuntime`。build.gradle 的 `configurations` 段必须显式
`clientRuntimeClasspath.extendsFrom clientLocalRuntime, localRuntime`，
否则 `localRuntime` 的 jar 只进 `runtimeClasspath`、不进 dev 客户端——`build` 仍成功，
但 `runClient` 启动时前置与测试套件全部缺席。改配置后用临时 Gradle task 打印
`clientRuntimeClasspath.files` 核对，不要只看 `dependencies` 报告（`files()` 依赖不在报告里显示）。
换机器后按文档重新放置即可，缺 jar 时 Gradle 的 `files()` 对缺失文件静默跳过，
dev client 会少加载对应 mod，但构建不会失败（前置 Thaumaturge 除外，它缺了 `compileJava` 会失败）。

升级 Thaumaturge 时四处同改：jar 文件名、build.gradle 的三条 files() 路径、
`gradle.properties` 的 `thaumaturge_version`、mods.toml 模板的 `versionRange`。
升级测试套件时改 `build.gradle` 里对应的 `localRuntime` 坐标；
只有 `libs/devmods-local/` 里的 9 个才需要改文件名。

### ModernUI 配置（dev client，只保留三项能力）

dev 环境只需要 ModernUI 的三项能力：**字体引擎**（TrueType/SDF 文本管线）、
**平滑字体**（抗锯齿 + 线性度量）、**现代化 tooltip 框**。
其余能力（GUI 模糊、界面动画、缩放键、emoji、加载提示音）在 dev 环境没有验证价值，
全部关闭，顺带削掉主菜单与界面渲染的一批非必要开销。

配置文件在 `run/config/ModernUI/`，随 `run/` 被 gitignore，换机器需重做。
两个文件都有 `linearMetrics`，必须同时为 `true`，否则文本度量按不同规则计算。

`text.toml`（保留项）：

| 键 | 值 | 对应能力 |
|---|---|---|
| `allowAsyncLayout` | `true` | 布局在工作线程计算，不阻塞主线程 |
| `smartSDFShaders` | `true` | 按设备空间像素密度在 SDF / 双线性间选择 |
| `computeDeviceFontSize` | `true` | 2D 下按变换矩阵精确算字号，缩小文本仍清晰 |
| `allowSDFTextIn2D` | `true` | 2D 允许走 SDF 文本 |
| `antiAliasing` | `true` | 字形栅格抗锯齿（平滑字体） |
| `linearMetrics` | `true` | 分数度量、无 hinting，线性缩放更平滑 |

`text.toml`（关闭项）：

| 键 | 值 | 关闭的能力 |
|---|---|---|
| `allowShadow` | `false` | 丢弃原版文本阴影，注释标注可提升性能 |
| `useTextShadersInWorld` | `false` | 3D 世界仍走原版文本管线，规避光影/渲染 mod 冲突 |
| `tweakExperienceText` | `false` | 改写经验等级文本描边，非必要 |

`client.toml`：

| 键 | 值 | 说明 |
|---|---|---|
| `[tooltip]` `enable` / `roundedShape` / `centerTitle` / `titleBreak` / `exactPositioning` | `true` | **保留**：现代化 tooltip 框 |
| `[screen] animationDuration` | `0` | 关背景色与模糊半径过渡动画 |
| `[screen] blurEffect` / `additionalBlurEffect` / `overrideVanillaBlur` | `false` | 关 GUI 高斯模糊，GPU 开销最大的一项 |
| `[screen] blurRadius` | `0` | 卷积核半径归零 |
| `[tooltip] borderCycleTime` | `0` | 关边框颜色循环 |
| `[general] ding` | `false` | 关加载完成提示音 |
| `[general] zoom` | `false` | 关 C 键缩放（OptiFine 同款，dev 不需要） |
| `[general] useNewGuiScale` | `false` | 保留原版 GUI 缩放按钮，不改缩放判定路径 |
| `[general] emojiShortcodes` | `false` | 关 Slack/Discord emoji 短码替换 |
| `[font] useColorEmoji` | `false` | 关彩色 emoji，用灰度（更快） |
| `[font] linearMetrics` | `true` | 与 `text.toml` 保持一致 |

`[tooltip]` 的 `colorFill` / `colorStroke` / `borderWidth` / `cornerRadius` 保持默认；
`shadowRadius` / `shadowOpacity` 配置注释明确标注不影响性能，不需要动。

以上是静态配置收敛，未做启动验证（按约定不主动起客户端）。
下次实机验证时重点看日志里 `res level` 抬升与 emoji 扫描是否消失。

## 常用命令

```bash
./gradlew build          # 全量构建（含 client 源集合并）
./gradlew compileJava    # 只编 main，快速查语法
./gradlew runClient      # 起 dev 客户端，手验 tooltip
```

Windows 可直接双击 `run_client.bat`（本地脚本，不进 git）。

## 启动故障

- **找不到 Thaumaturge 类**：检查网络能否访问 `https://cursemaven.com`，以及 `build.gradle` 中的 `curse.maven:thaumaturge-1628024:9452543` 坐标解析是否成功。
- **toolchain 解析失败**：`org.gradle.java.installations.paths` 指向的 JDK 不存在；
  改成实际路径，或删掉该行让 foojay 自动下载。
- **mod 加载报前置缺失**：dev client 没加载 `localRuntime` 的 Thaumaturge，
  检查 `neoForge.runs` 是否把 `localRuntime` 接进 `runtimeClasspath`（build.gradle 的 configurations 段）。
- **配置缓存报警**：本仓库未启用 configuration cache；若从参考仓库抄脚本时开启了，
  run 脚本可加 `--no-configuration-cache`。

## 日志噪声基线

一次正常启动稳定产出约 100 条 `ERROR`、70+ 条 `WARN`，全部来自其他 mod；
本模组 `thaumaturge_aspect_tooltip` 没有对应条目。先对照下表，别按条数判断环境坏了。

| 来源 | 量 | 内容 | 是否要处理 |
|---|---|---|---|
| `ftblibrary.icon.EntityIconLoader` | 每次重载 38 条 | `Missing entity icon settings for thaumaturge:*` | 否。FTB Library 为任务/队伍 UI 扫描全部实体，没配图标就记 ERROR |
| `guideme.internal.util.NavigationUtil` | 4 页 × 3 轮 | `Couldn't find icon ae2_pattern_disk:*_pattern_disk_provider` | 否。`ae2_pattern_disk` 的指南页引用了本版本不存在的物品 |
| `net.minecraft.Util` | 3 条 | `Invalid path in pack: .../README.md`、`.../OFL.txt` | 否。资源包里混入非资源文件，加载器跳过 |
| `world.level.storage.loot.LootDataType` | 4 条 | `Unknown registry key ...` | 否。`ae2_pattern_disk` 三个 provider 与 `extendedae:ex_emc_interface` 的战利品表无条件打包，方块按可选依赖条件注册 |

配套 `WARN` 也基本固定：`mixin` 探测不存在的兼容目标 25 条（Create / FunctionalStorage /
AdvancedAE / AE2LT / JEI 内部类）、`ItemIndex` 指南页引用未知物品 14 条、`ModelBakery` 缺
ModernUI 的 `project_builder*` 模型 3 条、`SoundEngine` 缺山羊角音效 2 条、`Commands` 传送
参数歧义 5 条（原版自带）。`JarSelector` 的 `passed in as source` **不是跳过**，是说明该 jar
已由 Gradle 直接提供、JarJar 不必再解包内嵌副本。

**tooltip 对照的混淆变量**：`better-advanced-tooltips` 是 KubeJS 的硬依赖（JiJ），会在每个
物品 tooltip 追加 fuel / tag / component 行。做要素 tooltip 对照前，把
`run/config/better-advanced-tooltips-client.toml` 的 `fuel_tooltip`、`tag_tooltip`、
`component_tooltip` 置 `false`。

`ae2lt` 已移除，`run/config/ae2lt-*.toml` 是残留文件，可删，不影响加载。
