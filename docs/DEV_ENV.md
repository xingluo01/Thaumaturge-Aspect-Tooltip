# 开发环境

## 前置

- JDK 21（toolchain 固定），可选 JDK 25 供 Gradle 自身使用。
  `gradle.properties` 的 `org.gradle.java.installations.paths` 指向实际安装路径。
- Thaumaturge 本体 jar 放进 `libs/`（见下方的开放环境 mod 一节）。
- Gradle 9.5 由 wrapper 自动下载；首次构建需联网。

## 开放环境 mod（dev runtime）

本模组的 dev client 必须加载前置 Thaumaturge，否则 tooltip 注入拿不到要素数据、启动也会失败。
本机没有公开 Maven 坐标，所以用本地 jar 同时做编译期与运行期依赖：

| 配置 | 作用 |
|---|---|
| `compileOnly files('libs/thaumaturge-...jar')` | main 源集编译期可见要素 API |
| `clientCompileOnly files('libs/thaumaturge-...jar')` | client 源集编译期可见 |
| `localRuntime files('libs/thaumaturge-...jar')` | dev client / server / data 运行期真正加载 |

jar 放在哪、从哪拷，见不进 git 的 `docs/LOCAL_DEPS.md`。
该文件不进版本库：换机器后按本机路径重新放置，libs/ 里没有 jar 时 `compileJava` 会失败。

升级 Thaumaturge 时四处同改：jar 文件名、build.gradle 的三条 files() 路径、
`gradle.properties` 的 `thaumaturge_version`、mods.toml 模板的 `versionRange`。

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
