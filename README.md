# Thaumaturge Tooltip Plus

Minecraft 1.21.1 / NeoForge 客户端附属模组。给「有要素数据、但原版 tooltip 没有文本要素行」的物品，
在 tooltip 上补一行文本要素显示。

## 它做什么

Thaumaturge 本体给部分物品显示要素信息，显示方式随物品而异：有的带文本行（`要素：秩序 x1`），
有的只在按住 Shift 时显示图形 Chip 版，还有的两者都没有。本模组补齐文本这一路——
只要物品有要素数据、却没有**文本**要素行，就追加一行；本体已经显示的那几类物品保持原样。

- 原生已有文本要素行：跳过，不重复添加。
- Shift 触发的图形 Chip 版：由本体负责，本模组不干预。
- 纯客户端功能：服务端不注册任何内容，加入服务器不需要对方安装。

## 前置

- Minecraft 1.21.1
- NeoForge 21.1.265+
- **Thaumaturge 1.1.0+**（required，未打包，需单独安装）

## 下载

- Modrinth：https://modrinth.com/mod/thaumaturge-tooltip-plus
- CurseForge：https://www.curseforge.com/projects/1825579（Project ID `1825579`）

## 构建

```bash
./gradlew build
```

编译期与 dev 运行期依赖的 Thaumaturge 1.1.0 已通过官方 CurseMaven 坐标自动解析，
直接运行 `./gradlew build` 即可产出 `build/libs/thaumaturge_tooltip_plus-<版本>.jar`。
其余 dev 运行期测试模组与环境说明见 `docs/DEV_ENV.md`（本机路径索引见不进 git 的 `docs/LOCAL_DEPS.md`）。

## 文档

先读 `AGENTS.md`。它是文档地图，指向架构、更新日志规范、发版手册与手动测试清单。

## 许可

AGPL-3.0，详见 `LICENSE`。
