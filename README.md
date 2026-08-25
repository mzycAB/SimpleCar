# SimpleCar

一个 Minecraft **Fabric 1.20.4** 模组，添加可驾驶的小汽车（类似可骑乘生物）。

## 功能

- **小汽车实体**：右键点击即可上车，潜行（Shift）下车
- **驾驶方式**：与骑马完全一致——鼠标控制方向，`W` 前进、`S` 倒车、`A/D` 侧移
- 车轮会随行驶滚动，前轮随转向偏转
- 可直接爬上 1 格高的方块，不会摔落受伤
- 受到铁傀儡式的金属音效，被摧毁后掉落小汽车物品
- 生命值 50，移速约为步行 3 倍

## 获取方式

- **合成**：白羊毛 × 3 + 熔炉 × 1 + 铁锭 × 5（合成表形状见游戏内 EMI/JEI/配方书）

  ```
  [羊毛] [羊毛] [羊毛]
  [铁锭] [熔炉] [铁锭]
  [铁锭] [空]   [铁锭]
  ```

- **创造模式物品栏**：交通运输分组
- 也有一个小汽车刷怪蛋（`/give @s simplecar:car_spawn_egg`）

## 使用

1. 手持小汽车对地面右键放置
2. 右键点击小汽车上车
3. WASD 驾驶，Shift 下车

## 构建

环境要求：JDK 17+（推荐 21）。

```bash
./gradlew build        # Windows: gradlew.bat build
```

构建产物位于 `build/libs/simplecar-1.0.0.jar`，安装 [Fabric Loader](https://fabricmc.net/use/) ≥ 0.15 与 [Fabric API](https://modrinth.com/mod/fabric-api) 后丢进 `mods` 文件夹即可。

如需重新生成纹理（`texture_gen.py`，仅依赖 Python 标准库）：

```bash
python texture_gen.py
```

## 技术实现

- `CarEntity` 继承 `PathAwareEntity`，复用原版骑乘控制管线（`travel()` 骑乘输入 + `VehicleMoveC2SPacket` 位置同步），无需 Mixin
- 模型为纯代码构建的盒模型（底盘、座舱、四轮、前后灯），渲染动画通过 `limbAnimator` 驱动车轮滚动
- 实现思路参考了开源模组 [Automobility](https://github.com/FoundationGames/Automobility)（MIT License）的方向盘/车轮动画设计，但采用了远比它简单的"生物骑乘"方案

## 许可证

MIT
