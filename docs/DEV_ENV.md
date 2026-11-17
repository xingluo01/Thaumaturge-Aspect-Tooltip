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

放在 `libs/devmods/`，只挂 `localRuntime`，与测试清单 `docs/MANUAL_TEST.md` 的用例对应：

| mod | 版本 | 测试用途 |
|---|---|---|
| Jade | 15.10.6 | tooltip 增强，U7 与冲突回归的主目标 |
| JadeAddons | 6.1.2 | Jade 附属（依赖 Jade） |
| JEI | 19.70.0.469 | U7 容器 / 查看器内悬停 |
| mezz_config | 0.7.1-standalone | JEI 19.70 的 required 前置 |
| mezz_config_gui | 0.5.5 | JEI 的可选前置（配置界面） |
| Architectury | 13.0.12 | 公共库 |
| Cloth Config | 15.0.140 | 公共库 |

jar 从整合包 `mods/` 拷贝；本机源路径见不进 git 的 `docs/LOCAL_DEPS.md`。
`sources` 里没有这些 mod，所以它们不影响编译；只影响 `runClient` 的加载列表。

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
升级测试套件时改 `libs/devmods/` 文件名与 build.gradle 对应的 `localRuntime` 行。

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
