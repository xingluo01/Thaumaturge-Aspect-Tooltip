# 发版手册

从打 tag 到三平台发布的操作与踩坑记录。只记事实与操作，不放待办（待办在 `TODO.md`）。
更新日志的写法见 `CHANGELOG_STYLE.md`。本仓库已通过 `.github/workflows/release.yml` 接入自动发布 CI。

## 一、发版流程（CI 自动版）

1. 把 `CHANGELOG_UNRELEASED.md` 里 `## [未发布]` 整段搬到 `CHANGELOG.md` 顶部，
   标题改成 `## [<版本>] - <YYYY-MM-DD>`（细则见 `CHANGELOG_STYLE.md` 第一节），
   并将 `CHANGELOG_UNRELEASED.md` 清回空骨架。
2. `gradle.properties` 的 `mod_version` 改成同一个版本号（本地构建产物用它；CI 用 tag 覆盖）。
3. 本地运行 `./gradlew build` 核对产物 `build/libs/thaumaturge_tooltip_plus-<版本>.jar`。
4. 提交并推送后打 tag：`git tag vX.Y.Z && git push origin vX.Y.Z`。
5. CI（`.github/workflows/release.yml`）依次执行：解析 tag 版本 → 校验凭据 → JDK 21 →
   构建 → 抽取更新日志 → Modrinth（minotaur）→ CurseForge → GitHub Release。
6. 发布后按第六节核对。

重跑同一个 tag：Modrinth 会因版本号已存在而失败，CurseForge 不去重、会再传一份。
发布中断后优先升版本号重发，不要直接重跑同一个 tag。

## 二、仓库变量与密钥

在 GitHub 仓库 `Settings → Secrets and variables → Actions` 配置：

| 类型 | 名称 | 说明 |
|---|---|---|
| Secrets | `MODRINTH_TOKEN` | Modrinth PAT，至少勾 `VERSION_CREATE`（可选加 `VERSION_WRITE` / `PROJECT_WRITE`） |
| Secrets | `CURSEFORGE_TOKEN` | CurseForge 站点账户 UUID token（`curseforge.com/account/api-tokens`，非 console API key） |
| Variables | `MODRINTH_PROJECT_ID` | 可选覆盖，未配置时默认 `thaumaturge-tooltip-plus` |
| Variables | `CURSEFORGE_PROJECT_ID` | 可选覆盖，未配置时默认 `1825579`（必须为数字 ID） |

## 三、Modrinth

- 项目页：https://modrinth.com/mod/thaumaturge-tooltip-plus（slug：`thaumaturge-tooltip-plus`）。
- 审核通过前对公开 API 返回 404，属正常（draft 不可见）。
- `build.gradle` 接入 `com.modrinth.minotaur`（`2.9.0`）；CI 执行 `./gradlew modrinth --no-configuration-cache`。
- **不声明 `required.project 'thaumaturge'`**：Thaumaturge 本体未上架 Modrinth（上游 `gradle.properties` 中 `modrinth_id` 为空），minotaur 解析不到 slug 会直接失败；前置依赖由 jar 内 `neoforge.mods.toml` 与项目页正文说明。
- CI 未就绪时的本地后备：`MODRINTH_TOKEN=... MODRINTH_PROJECT_ID=thaumaturge-tooltip-plus ./gradlew modrinth`。

## 四、CurseForge

- 项目页：https://www.curseforge.com/projects/1825579（数字项目 ID：`1825579`）。
- 上传接口只认数字项目 ID 与站点 UUID token（`X-Api-Token`）；Eternal API 的 `x-api-key`（`$2a$10$…`）不能用于上传。
- CI 使用 `itsmeow/curseforge-upload@v3.1.2`：
  - `game_versions: '11779,10150,9638'`（传统接口数字 ID：`1.21.1=11779`、`NeoForge=10150`、`Client=9638`；本模组为纯客户端功能，不勾选 `Server=9639`）。
  - `relations: 'thaumaturge:requiredDependency'`（CurseForge 官方 slug `thaumaturge`，Project ID `1628024`）。
- 新文件需经过 CurseForge 人工审核，上传成功后短时间在公开文件列表不可见属正常，以 CI 上传步骤成功为准。

## 五、版本号口径

- tag 用 `vX.Y.Z`，`mods.toml` 与 `mod_version` 用 `X.Y.Z`（不带 `v`）。
- 编译期前置 Thaumaturge 走 CurseMaven 坐标（当前 `curse.maven:thaumaturge-1628024:9452543`，对应 1.1.0）。
- 前置下限变动时，`gradle.properties` 的 `thaumaturge_version` 与 `build.gradle` 的 CurseMaven 坐标同步更新。

## 六、发布后核对

- CI 日志中有 `changelog source: CHANGELOG.md (N chars)`，且标题下方正文首行是小节名（`### 调整与依赖` 一类），不是 commit 主题。
- 产物 jar 内 `META-INF/neoforge.mods.toml` 的 `modId`、`version`、`license`、`displayName`、依赖区间正确，`META-INF/LICENSE` 为 AGPL-3.0 全文。
- CurseForge 文件页 Relations 显示 `Thaumaturge` 为 required dependency，环境标为 Client。
- Modrinth 项目页正文明确注明前置依赖 `Thaumaturge 1.1.0+` 与许可 `AGPL-3.0`。
