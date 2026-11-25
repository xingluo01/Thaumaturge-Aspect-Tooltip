# 待办

只放未完成事项。已完成的事写进更新日志或归档文档，不留在这里。

## 实机验证（当前）

- [ ] `docs/MANUAL_TEST.md` 的用例全部实机跑一遍，重点 U1 / U2 / U5：普通物品是否补出行、
      本体已显示文本的三类物品是否不重复、多要素数量与排序是否正确。
- [ ] 按实机结果决定是否需要开关：若「所有有要素的物品都显示」在实际整合包里太吵，
      加一个客户端配置项收窄范围（例如只对指定标签或只对非食物生效）。

## 发布前

- [ ] 在 GitHub 仓库配置 `MODRINTH_TOKEN` 与 `CURSEFORGE_TOKEN`（及可选 Variables `MODRINTH_PROJECT_ID` / `CURSEFORGE_PROJECT_ID`），推送 `v0.1.2` tag 首次验证 CI 全链路。
