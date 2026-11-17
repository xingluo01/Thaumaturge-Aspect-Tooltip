# AGENTS.md

本仓库的文档地图与动手前必读的硬约定。给进入仓库的 AI 代理，也给人看。
先读本页再动手；这里只做索引，细节都在被指向的文档里。

## 这是什么

Thaumaturge Aspect Tooltip，Minecraft 1.21.1 / NeoForge 客户端附属模组。
为「有要素数据、但原版 tooltip 没有文本要素行」的物品，在 tooltip 上补一行文本要素显示。
依赖 Thaumaturge 本体的要素 API；Shift 触发的图形 Chip 版由本体负责，本模组不碰。

**基线：Minecraft 1.21.1 · NeoForge 21.1.265 · Java 21 · ModDevGradle 2.0.144 · Gradle 9.5。**
权威来源是 `gradle/libs.versions.toml` 与 `gradle.properties`，这一行只是速览。

## 一、规则类文档（进 git，动手前必读）

| 文档 | 什么时候读 |
|---|---|
| `docs/ARCHITECTURE.md` | 改任何代码前：源集职责、tooltip 注入点、要素数据来源、客户端边界 |
| `docs/CHANGELOG_STYLE.md` | 改任一份更新日志前：两份文件分工、六小节、条目长度与发版搬运 |
| `docs/RELEASE.md` | 建 GitHub Release 前：版本号、CI 发布链与已知坑 |
| `docs/TODO.md` | 想知道「还剩什么事没做」时：只放未完成事项 |
| `docs/MANUAL_TEST.md` | 玩家手动测试：安装、用例、验证点与回归清单 |
| `docs/DEV_ENV.md` | 搭 dev 环境：开放运行期 mod、JDK、常见启动故障 |
| `CHANGELOG_UNRELEASED.md` | **待发改动写这里**：未发布的累计调整，发版时整段并入 `CHANGELOG.md` |
| `CHANGELOG.md` | 已发布内容的留档；改格式前先读 `docs/CHANGELOG_STYLE.md` |
| `README.md` | 项目自述、功能边界与构建入口 |

`docs/LOCAL_DEPS.md` 是本机依赖的绝对路径索引，换机器要改，不进 git。

## 二、动手前必须知道的硬约定

- **语言**：注释、`docs/` 与两份更新日志一律中文；代码标识符与游戏内英文文案除外。
- **客户端边界**：tooltip 注入代码放 `src/client/java`；`src/main` 不得引用客户端类。
  本模组是纯客户端功能，服务端不注册任何东西。
- **不改本体行为**：只追加缺失的文本行，不覆盖、不隐藏 Thaumaturge 原生的任何 tooltip 内容。
  原生已有文本行时必须跳过，避免重复。Shift 图形版一律交给本体。
- **数据来源**：要素只能经 Thaumaturge 的 `api.aspect` 包读取，不反射内部实现、不复制要素表。
  该包名与调用方式见 `docs/ARCHITECTURE.md`，实际签名以 `libs/` 里的 jar 为准。
- **依赖来源**：dev 运行期依赖只要有官方 Maven / CurseMaven / Modrinth 源，就必须用坐标声明，
  禁止把 jar 放进 `libs/devmods-local/` 或从整合包拷贝。只有确认无可用官方坐标时才允许本地 jar，
  并在 `docs/LOCAL_DEPS.md` 登记版本、来源路径与哈希。
  Modrinth 坐标的 version 段必须用官方 version ID，禁止用 version_number：同一版本号可同时存在
  NeoForge / Fabric 构件，裸版本号会解析错 loader（见 `docs/DEV_ENV.md` 与参考仓库的 JECharacters 注释）。
- **生成资源不手改**：`neoforge.mods.toml` 由 `src/main/templates` 经 `generateModMetadata` 生成。
- **依赖前置**：`thaumaturge` 在 mods.toml 里是 required，版本下限同步 `gradle.properties` 的
  `thaumaturge_version`。本模组不打包本体。
- **验证**：代码改动至少跑 `./gradlew build`；界面行为必须按 `docs/MANUAL_TEST.md` 起 dev client 手验。

## 三、常用命令

```bash
./gradlew build              # 全量构建
./gradlew compileJava        # 只编 main
./gradlew runClient          # 实机验证（需先在 libs/ 放好 Thaumaturge jar）
```

Windows 下可直接双击 `run_client.bat`（本地脚本，不进 git）。

## 四、读上游与依赖源码之前

- 先确认版本：`gradle/libs.versions.toml` 与 `gradle.properties` 是唯一事实源。
- 读 Thaumaturge 的 API 时，以 `libs/` 里当前版本 jar 为准；不要用其他版本或其他重制核心的类做结论。
- 本仓库只认 `thaumaturge` 这个 modid 的要素体系，不兼容其他神秘重制核心（三花、Thavma、Thaumon 等）。
