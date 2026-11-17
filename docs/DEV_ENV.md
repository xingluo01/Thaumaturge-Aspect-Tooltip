# 开发环境

## 前置

- JDK 21（toolchain 固定），可选 JDK 25 供 Gradle 自身使用。
  `gradle.properties` 的 `org.gradle.java.installations.paths` 指向实际安装路径。
- Thaumaturge 本体 jar 放进 `libs/`（见下方的开放环境 mod 一节）。
- Gradle 9.5 由 wrapper 自动下载；首次构建需联网。

## 开放环境 mod（dev runtime）

dev client 加载的 mod 全部走本地 jar + `localRuntime`，不进成品 jar、不进 git。
之所以不用公开 Maven 坐标：本机 maven.modrinth 不通、cursemaven 需逐条试 file id，
而本地 jar 与整合包版本完全一致，手动测试才反映玩家真实环境（参考仓库 AE2-Pattern-Disk
的 AECS 依赖也是这套本地 jar 做法）。

### 前置（必须）

Thaumaturge 1.1.0 是 required 前置，缺它 tooltip 注入拿不到要素数据、启动也会失败。
本机没有公开 Maven 坐标，用本地 jar 同时做编译期与运行期依赖：

| 配置 | 作用 |
|---|---|
| `compileOnly files('libs/thaumaturge-...jar')` | main 源集编译期可见要素 API |
| `clientCompileOnly files('libs/thaumaturge-...jar')` | client 源集编译期可见 |
| `localRuntime files('libs/thaumaturge-...jar')` | dev client / server / data 运行期真正加载 |

### 测试套件（dev-only）

**以 Maven 坐标声明为主**（`build.gradle` 的 `localRuntime '...'`），不用往仓库塞 jar。
Coordinates 优先 CurseMaven，其余走 Modrinth（通过 `api.modrinth.com/maven`）、
BlameJared（JEI）、Latvian（KubeJS/Rhino）。版本与整合包一致，测试才反映玩家真实环境。
少数没有可用公开坐标的 mod 才放本地 jar 到 `libs/devmods-local/`（当前 9 个）。
名册如下（41 个 mod：32 个坐标 + 9 个本地 jar）：

| modid | 版本 | 测试用途 |
|---|---|---|
| jade | 15.10.6 | tooltip 增强，U7 与冲突回归的主目标 |
| jadeaddons | 6.1.2 | Jade 附属（依赖 Jade） |
| jei | 19.70.0.469 | U7 容器 / 查看器内悬停 |
| mezz_config | 0.7.1-standalone | JEI 19.70 的 required 前置 |
| mezz_config_gui | 0.5.5 | JEI 的可选前置（配置界面） |
| architectury | 13.0.12 | 公共库 |
| cloth_config | 15.0.140 | 公共库 |
| modernui | 3.13.0.1 | UI 库，Jade / FTB 系界面依赖 |
| sodium | 0.8.13 | 渲染优化下验证 tooltip |
| itemzoomer | 2.0.1 | 直接改 tooltip 呈现 |
| jecharacters | 4.5.30 | JEI 拼音搜索，文本叠加 |
| fusion | 1.3.16 | Connected Textures |
| mod_menu | 1.0.2 | 模组列表界面 |
| ftblibrary | 2101.1.39 | FTB 系界面库 |
| kubejs | 2101.7.2 | 脚本环境，可动态改 tooltip 做对照 |
| rhino | 2101.2.7 | KubeJS 的 JS 引擎 |
| ae2jeiintegration | 1.2.1 | 扩展 JEI 配方类别 |
| polymorph | 1.1.0 | 给物品附加提示，测 tooltip 叠加 |
| ae2 | 19.2.19 | 大量带要素的物品样本 |
| guideme | 21.1.22 | AE2 的指南书库与硬前置 |
| ae2wtlib | 19.5.1 | AE2 无线终端 |
| extendedae | 2.2.43 | 扩展样板供应器与磁盘 |
| ae2omnicells | 1.1.6 | 大容量存储单元 |
| curios | 9.5.1 | 饰品栏，tooltip 叠加常见场景 |
| geckolib | 4.9.3 | 动画库，多 mod 前置 |
| modernfix | 5.27.27 | 启动优化与诊断 |
| spark | 1.10.124 | 性能采样，定位 tooltip 卡顿 |

**神秘使附属（10 个，整合包装有的全部纳入）**：`classic_thaumonomicon` 1.0.2、
`kubethaum` 1.3.1、`thaumaturgeadditions` 1.2.1、`thaumictracker` 1.1、
`automagia` 0.8.7、`thaumicbases` 3.6.023.5、`thaumaturge_tinkerer` 0.3、
`thaumadditions` 0.18.26、`thaumictinkerer` 0.1.2-beta、`thaumicenergistics_ce` 2.7.5.4。
全部依赖本体 `thaumaturge`（已在 `libs/` 根，compileOnly + localRuntime）。

**样板磁盘与闪电科技（4 个）**：`ae2_pattern_disk` 0.9.13（整合包 jar）；
`ae2lt` 2.1.2 + `thunderbolt` 2.0.3 + `ae2ltpp` 1.2.2（整合包无，从 Modrinth CDN 下载，
版本取参考仓库 AE2-Pattern-Disk 验证过的稳定版，不用更新的 beta）。
`ae2lt` 的必需前置 `ae2wtlib_api` 与 `thunderbolt` 均已就位。

坐标来源：主流 mod 由参考仓库 AE2-Pattern-Disk / TE4 验证过的 curse.maven 坐标声明；
其余用 jar 的 SHA-1 在 Modrinth API 反查出精确版本坐标。
9 个本地 jar（`ExtendedAE`、`ae2jeiintegration`、`automagia`、`classic_thaumonomicon`、
`kubethaum`、`thaumaturge_tinkerer`、`thaumaturgeadditions`、`thaumictinkerer`、`thaumicbases`）
没有可用公开坐标，仍从整合包拷贝。本机路径见不进 git 的 `docs/LOCAL_DEPS.md`。
这些依赖不在 `sources` 里，不影响编译，只影响 `runClient` 的加载列表。

**仓库配置**：`repositories` 段为这些坐标加了 cursemaven、api.modrinth.com/maven、
BlameJared、Latvian、JitPack、Shedaniel。本机 `maven.modrinth.com` 不通（TLS 连接被关闭），
故用 `api.modrinth.com/maven` 提供同一 `maven.modrinth` group，不要改回旧域名。

**NeoForge 基线已升到 21.1.265**：`thaumicbases` 声明 `neoforge [21.1.265,)`，
低于此版本 FML 会拒绝加载。21.1.265 也是整合包实例实际使用的版本
（`axolotl_config.json` 的 `content_set.loader_version`），升基线使 dev 与玩家环境一致。

### 汉化资源包（dev client）

不走 `localRuntime`，放在 `run/resourcepacks/`，由 `run/options.txt` 的 `resourcePacks` 行启用：

| 包 | 作用 |
|---|---|
| `Minecraft-Mod-Language-Modpack-Converted-1.21.1.zip` | 通用 mod 中文层，在前 |
| `Extra Mods ZH_CH.zip` | 本机补充汉化（17 个命名空间），在后覆盖 |

`run/` 被 gitignore，换机器需重新放置这两个 zip 与 options.txt，
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
