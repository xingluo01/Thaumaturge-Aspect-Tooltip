# 发版手册

从打 tag 到发布的操作与踩坑记录。只记事实与操作，不放待办（待办在 `TODO.md`）。
更新日志的写法见 `CHANGELOG_STYLE.md`。本仓库尚未接 CI，以下为发布流程与待接项。

## 一、发版流程（当前手工版）

1. 把 `CHANGELOG_UNRELEASED.md` 里 `## [未发布]` 整段搬到 `CHANGELOG.md` 顶部，
   标题改成 `## [<版本>] - <YYYY-MM-DD>`（细则见 `CHANGELOG_STYLE.md` 第一节）。
2. `gradle.properties` 的 `mod_version` 改成同一个版本号。
3. `./gradlew build`，产出 `build/libs/thaumaturge_tooltip_plus-<版本>.jar`。
4. `git tag vX.Y.Z && git push origin vX.Y.Z`。
5. 上传产物到 Modrinth / CurseForge，贴 `CHANGELOG.md` 里对应版本段作说明。

## 二、待接 CI

参考仓库的发布链（本仓库尚未实现，动手时照抄后再改平台 id）：

- AE2-Pattern-Disk：`.github/workflows/release.yml`，tag 触发 → JDK 21 → 构建 →
  Modrinth（minotaur）→ CurseForge → GitHub Release，三平台全自动。
- 仓库变量：`MODRINTH_PROJECT_ID` / `CURSEFORGE_PROJECT_ID`（vars），
  `MODRINTH_TOKEN` / `CURSEFORGE_TOKEN`（secrets）。
- CurseForge 双 API 不通用：上传走传统接口 + 站点 UUID token，元数据查询走 Eternal API + console key。
  `gameVersions` 要数字 id（1.21.1=11779 / NeoForge=10150 / Client=9638 / Server=9639）。
  细节见 `E:\GitHub\AE2-Pattern-Disk\docs\RELEASE.md`。

## 三、版本号口径

- tag 用 `vX.Y.Z`，mods.toml 与 `mod_version` 用 `X.Y.Z`（不带 `v`）。
- 前置下限变动时，`gradle.properties` 的 `thaumaturge_version` 与
  `src/main/templates/META-INF/neoforge.mods.toml` 的 `versionRange` 要一起改。

## 四、发布后核对

- `CHANGELOG.md` 的发布正文首行是小节名（`### 新增` 一类），不是 commit 主题。
- 产物 jar 内 `META-INF/neoforge.mods.toml` 的 `modId`、`version`、依赖区间正确。
- Modrinth / CurseForge 页把 Thaumaturge 标为 required dependency。
