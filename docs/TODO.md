# 待办

只放未完成事项。已完成的事写进更新日志或归档文档，不留在这里。

## 雏形阶段（当前）

- [ ] 起 dev client，确认 `libs/` 里的 Thaumaturge jar 能被 `localRuntime` 加载、游戏能进主菜单。
- [ ] 用 javap 核对本版本 `api.aspect` 包里「按 ItemStack 取要素列表」的实际方法签名，
      写入 `ARCHITECTURE.md` 后再动注入代码。
- [ ] 实现 tooltip 注入骨架：读要素列表 → 判断原生是否已有文本行 → 无则追加文本。
- [ ] 处理「有要素数据但无文本行」的判定口径（见 `ARCHITECTURE.md` 待定项）。
- [ ] 本地化：中文 zh_cn 与英文 en_us 的要素行文案。
- [ ] `docs/MANUAL_TEST.md` 里的用例全部实机跑一遍。

## 发布前

- [ ] 接 GitHub Release CI（照 `docs/RELEASE.md`）。
- [ ] Modrinth / CurseForge 项目页与依赖声明。
- [ ] 图标 `icon.png`（mods.toml 的 `logoFile` 目前注释掉）。
