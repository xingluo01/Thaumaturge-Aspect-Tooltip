# 开发环境

## 前置

- JDK 21（toolchain 固定），可选 JDK 25 供 Gradle 自身使用。
  `gradle.properties` 的 `org.gradle.java.installations.paths` 指向实际安装路径。
- Thaumaturge 本体 jar 放进 `libs/`（见下方的开放环境 mod 一节）。
- Gradle 9.5 由 wrapper 自动下载；首次构建需联网。

## 开放环境 mod（dev runtime）

dev client 加载的 mod 全部走 `localRuntime`，不进成品 jar、不进 git。
**硬规则：有官方 Maven / CurseMaven / Modrinth 源的依赖必须用坐标声明，禁止把 jar 放进
`libs/devmods-local/` 或从整合包拷贝。** 只有确认无可用官方坐标时才允许本地 jar，
且必须在 `docs/LOCAL_DEPS.md` 登记版本、来源路径与哈希。

### 前置（必须）

Thaumaturge 1.1.0 是 required 前置，缺它 tooltip 注入拿不到要素数据、启动也会失败。
本机没有公开 Maven 坐标，用本地 jar 同时做编译期与运行期依赖：

| 配置 | 作用 |
|---|---|
| `compileOnly files('libs/thaumaturge-...jar')` | main 源集编译期可见要素 API |
| `clientCompileOnly files('libs/thaumaturge-...jar')` | client 源集编译期可见 |
| `localRuntime files('libs/thaumaturge-...jar')` | dev client / server / data 运行期真正加载 |

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
少数没有可用公开坐标的 mod 才放本地 jar 到 `libs/devmods-local/`（当前 9 个）。
名册按用途简单分组（版本以 `build.gradle` 为准；运行时另带入少量传递依赖）：

| 分组 | modid（版本） | 用途 |
|---|---|---|
| 本体与必需前置 | `thaumaturge` 1.1.0、`glodium` 1.21-2.2、`lithostitched` 1.8.1、`guideme` 21.1.22、`mezz_config` 0.7.1-standalone、`mezz_config_gui` 0.5.5、`ae2wtlib_api` 19.5.1 | 本模组前置与运行期硬依赖；本体 jar 在 `libs/` 根 |
| 公共库与脚本 | `architectury` 13.0.11、`cloth_config` 15.0.140、`curios` 9.5.1、`geckolib` 4.9.3、`ftblibrary` 2101.1.39、`modernui` 3.13.0.1、`kubejs` 2101.7.2、`rhino` 2101.2.7 | 公共库、UI 底座、脚本环境，可动态改 tooltip 做对照 |
| 渲染、性能与诊断 | `sodium` 0.8.13、`fusion` 1.3.16、`modernfix` 5.27.27、`spark` 1.10.124、`mod_menu` 1.0.2 | 验证优化环境下的 tooltip，并用 spark 定位卡顿 |
| Tooltip / JEI 验证 | `jade` 15.10.6、`jadeaddons` 6.1.2、`jei` 19.70.0.469、`jecharacters` 4.5.30、`itemzoomer` 2.0.1、`polymorph` 1.1.0 | tooltip 增强、文本叠加、拼音搜索与多结果提示的主要对照目标 |
| AE2 样板与存储 | `ae2` 19.2.19、`ae2jeiintegration` 1.2.1、`ae2wtlib` 19.5.1、`extendedae` 2.2.43、`ae2omnicells` 1.1.6、`ae2_pattern_disk` 0.9.13 | 大量带要素物品样本、样板/磁盘/无线终端场景 |
| 神秘使附属 | `classic_thaumonomicon` 1.0.2、`kubethaum` 1.3.1、`thaumaturgeadditions` 1.2.1、`thaumictracker` 1.1、`automagia` 0.8.7、`thaumicbases` 3.6.023.5、`thaumaturge_tinkerer` 0.3、`thaumadditions` 0.18.26、`thaumictinkerer` 0.1.2-beta、`thaumicenergistics_ce` 2.7.5.4 | 全部依赖本体 `thaumaturge`；覆盖神秘使体系内的 tooltip 场景 |

`ae2_pattern_disk` 为整合包外补入的稳定版，版本取参考仓库 AE2-Pattern-Disk 验证过的值。
闪电科技三件（`ae2lt`、`ae2ltpp`、`thunderbolt`）已从 dev 环境移除，以排查主菜单卡顿；
需要时再单独加回。

坐标来源：主流 mod 由参考仓库 AE2-Pattern-Disk / TE4 验证过的 curse.maven 坐标声明；
其余用 jar 的 SHA-1 在 Modrinth API 反查出精确版本坐标。
9 个本地 jar（`ExtendedAE`、`ae2jeiintegration`、`automagia`、`classic_thaumonomicon`、
`kubethaum`、`thaumaturge_tinkerer`、`thaumaturgeadditions`、`thaumictinkerer`、`thaumicbases`）
没有可用公开坐标，仍从整合包拷贝。
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
文件夹版；若主菜单仍卡，再依次 A/B GuideME / `ae2_pattern_disk`、KubeJS / Rhino。

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

## 常用命令

```bash
./gradlew build          # 全量构建（含 client 源集合并）
./gradlew compileJava    # 只编 main，快速查语法
./gradlew runClient      # 起 dev 客户端，手验 tooltip
```

Windows 可直接双击 `run_client.bat`（本地脚本，不进 git）。

## 启动故障

- **找不到 Thaumaturge 类**：`libs/` 里没有 jar，或文件名与 build.gradle 的 files() 路径不一致。
- **toolchain 解析失败**：`org.gradle.java.installations.paths` 指向的 JDK 不存在；
  改成实际路径，或删掉该行让 foojay 自动下载。
- **mod 加载报前置缺失**：dev client 没加载 `localRuntime` 的 Thaumaturge，
  检查 `neoForge.runs` 是否把 `localRuntime` 接进 `runtimeClasspath`（build.gradle 的 configurations 段）。
- **配置缓存报警**：本仓库未启用 configuration cache；若从参考仓库抄脚本时开启了，
  run 脚本可加 `--no-configuration-cache`。
