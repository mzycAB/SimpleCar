"""Generates all pixel-art textures for the SimpleCar mod (pure stdlib, no PIL)."""
import os
import struct
import zlib

OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src", "main", "resources", "assets", "simplecar")

# Color themes: one entry per Minecraft concrete color.
# "red" keeps the original palette and file name; every other theme only
# replaces the red body colors with the concrete color, everything else stays.
THEMES = {
    "red": {
        "name": "car",
        "RED": (183, 28, 28, 255),
        "RED_DARK": (127, 13, 13, 255),
        "RED_LIGHT": (229, 57, 53, 255),
    },
    "white": {
        "name": "car_white",
        "RED": (245, 245, 245, 255),
        "RED_DARK": (198, 202, 208, 255),
        "RED_LIGHT": (255, 255, 255, 255),
    },
    "gray": {
        "name": "car_gray",
        "RED": (158, 158, 158, 255),
        "RED_DARK": (105, 105, 105, 255),
        "RED_LIGHT": (189, 189, 189, 255),
    },
    "black": {
        "name": "car_black",
        "RED": (8, 10, 15, 255),
        "RED_DARK": (4, 5, 8, 255),
        "RED_LIGHT": (94, 96, 99, 255),
    },
    "blue": {
        "name": "car_blue",
        "RED": (45, 47, 143, 255),
        "RED_DARK": (25, 26, 79, 255),
        "RED_LIGHT": (119, 120, 182, 255),
    },
    "brown": {
        "name": "car_brown",
        "RED": (96, 60, 32, 255),
        "RED_DARK": (53, 33, 18, 255),
        "RED_LIGHT": (152, 128, 110, 255),
    },
    "cyan": {
        "name": "car_cyan",
        "RED": (21, 119, 136, 255),
        "RED_DARK": (12, 65, 75, 255),
        "RED_LIGHT": (103, 167, 178, 255),
    },
    "green": {
        "name": "car_green",
        "RED": (73, 91, 36, 255),
        "RED_DARK": (40, 50, 20, 255),
        "RED_LIGHT": (137, 148, 113, 255),
    },
    "light_blue": {
        "name": "car_light_blue",
        "RED": (36, 137, 199, 255),
        "RED_DARK": (20, 75, 109, 255),
        "RED_LIGHT": (113, 178, 219, 255),
    },
    "light_gray": {
        "name": "car_light_gray",
        "RED": (125, 125, 115, 255),
        "RED_DARK": (69, 69, 63, 255),
        "RED_LIGHT": (170, 170, 164, 255),
    },
    "lime": {
        "name": "car_lime",
        "RED": (94, 169, 24, 255),
        "RED_DARK": (52, 93, 13, 255),
        "RED_LIGHT": (150, 199, 105, 255),
    },
    "magenta": {
        "name": "car_magenta",
        "RED": (169, 48, 159, 255),
        "RED_DARK": (93, 26, 87, 255),
        "RED_LIGHT": (199, 121, 193, 255),
    },
    "orange": {
        "name": "car_orange",
        "RED": (224, 97, 1, 255),
        "RED_DARK": (123, 53, 1, 255),
        "RED_LIGHT": (235, 152, 90, 255),
    },
    "pink": {
        "name": "car_pink",
        "RED": (214, 101, 143, 255),
        "RED_DARK": (118, 56, 79, 255),
        "RED_LIGHT": (228, 155, 182, 255),
    },
    "purple": {
        "name": "car_purple",
        "RED": (100, 32, 156, 255),
        "RED_DARK": (55, 18, 86, 255),
        "RED_LIGHT": (154, 110, 191, 255),
    },
    "yellow": {
        "name": "car_yellow",
        "RED": (241, 175, 21, 255),
        "RED_DARK": (133, 96, 12, 255),
        "RED_LIGHT": (246, 203, 103, 255),
    },
}

RED = THEMES["red"]["RED"]
RED_DARK = THEMES["red"]["RED_DARK"]
RED_LIGHT = THEMES["red"]["RED_LIGHT"]
BLACK = (20, 20, 20, 255)
DARKGRAY = (55, 58, 62, 255)
GRAY = (120, 124, 130, 255)
GRAY_LIGHT = (170, 175, 182, 255)
WHITE_STRIPE = (236, 236, 236, 255)
GLASS = (140, 198, 236, 255)
GLASS_DARK = (96, 150, 190, 255)
GLASS_LIGHT = (205, 233, 250, 255)
TIRE = (30, 30, 32, 255)
TIRE_DARK = (18, 18, 20, 255)
HUB = (158, 162, 168, 255)
HUB_DARK = (90, 94, 100, 255)
HEADLIGHT = (255, 249, 196, 255)
TAILLIGHT = (255, 60, 50, 255)
SHADOW = (0, 0, 0, 70)


class Canvas:
    def __init__(self, w, h):
        self.w = w
        self.h = h
        self.px = [[(0, 0, 0, 0)] * w for _ in range(h)]

    def rect(self, x0, y0, x1, y1, color):
        for y in range(max(0, y0), min(self.h, y1)):
            for x in range(max(0, x0), min(self.w, x1)):
                self.px[y][x] = color

    def pixel(self, x, y, color):
        if 0 <= x < self.w and 0 <= y < self.h:
            self.px[y][x] = color

    def disc(self, cx, cy, r, color):
        for y in range(self.h):
            for x in range(self.w):
                if (x - cx) ** 2 + (y - cy) ** 2 <= r * r:
                    self.px[y][x] = color

    def save(self, path):
        raw = b"".join(b"\x00" + b"".join(struct.pack("4B", *p) for p in row) for row in self.px)

        def chunk(tag, data):
            c = struct.pack(">I", len(data)) + tag + data
            return c + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)

        png = b"\x89PNG\r\n\x1a\n"
        png += chunk(b"IHDR", struct.pack(">IIBBBBB", self.w, self.h, 8, 6, 0, 0, 0))
        png += chunk(b"IDAT", zlib.compress(raw, 9))
        png += chunk(b"IEND", b"")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "wb") as f:
            f.write(png)
        print("wrote", path)


def box(u, v, w, h, d):
    """Standard Minecraft box UV: returns half-open rects for each face."""
    return {
        "top": (u + d, v, u + d + w, v + d),
        "bottom": (u + d + w, v, u + 2 * d + w, v + d),
        "west": (u, v + d, u + d, v + d + h),
        "north": (u + d, v + d, u + d + w, v + d + h),
        "east": (u + d + w, v + d, u + 2 * d + w, v + d + h),
        "south": (u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h),
    }


def glass_with_shine(cv, rect):
    x0, y0, x1, y1 = rect
    cv.rect(x0, y0, x1, y1, GLASS)
    for y in range(y0, y1):
        for x in range(x0, x1):
            if (x - y) % 4 == 0:
                cv.pixel(x, y, GLASS_LIGHT)


def make_entity_texture(theme):
    RED = theme["RED"]
    RED_DARK = theme["RED_DARK"]
    RED_LIGHT = theme["RED_LIGHT"]
    cv = Canvas(128, 128)

    # ---- chassis: 24w x 8h x 36d at uv(0,0) ----
    body = box(0, 0, 24, 8, 36)
    x0, y0, x1, y1 = body["top"]
    cv.rect(x0, y0, x1, y1, RED)
    cv.rect(x0, y0, x1, y0 + 2, RED_DARK)          # shading at both ends
    cv.rect(x0, y1 - 2, x1, y1, RED_DARK)
    cv.rect(x0 + 5, y0 + 2, x0 + 7, y1 - 2, RED_DARK)   # racing stripes
    cv.rect(x0 + 17, y0 + 2, x0 + 19, y1 - 2, RED_DARK)
    cv.rect(x0 + 6, y0 + 2, x0 + 7, y1 - 2, RED_LIGHT)

    x0, y0, x1, y1 = body["bottom"]
    cv.rect(x0, y0, x1, y1, DARKGRAY)
    for y in range(y0 + 3, y1, 6):
        cv.rect(x0, y, x1, y + 1, BLACK)

    for side in ("west", "east"):
        x0, y0, x1, y1 = body[side]
        cv.rect(x0, y0, x1, y1, RED)
        cv.rect(x0, y0, x1, y0 + 1, RED_LIGHT)      # top highlight
        cv.rect(x0, y1 - 2, x1, y1, BLACK)          # side skirt
        cv.rect(x0 + 2, y0 + 4, x1 - 2, y0 + 6, WHITE_STRIPE)  # side stripe
        cv.rect(x0 + 17, y0 + 1, x0 + 18, y1 - 2, RED_DARK)    # door split

    x0, y0, x1, y1 = body["north"]  # front
    cv.rect(x0, y0, x1, y1, RED)
    cv.rect(x0, y0, x1, y0 + 1, RED_LIGHT)
    cv.rect(x0, y1 - 2, x1, y1, BLACK)              # bumper
    cv.rect(x0 + 9, y0 + 2, x1 - 9, y0 + 5, BLACK)  # grille
    cv.pixel(x0 + 2, y0 + 2, RED_DARK)
    cv.pixel(x1 - 3, y0 + 2, RED_DARK)

    x0, y0, x1, y1 = body["south"]  # back
    cv.rect(x0, y0, x1, y1, RED)
    cv.rect(x0, y0, x1, y0 + 1, RED_DARK)
    cv.rect(x0, y1 - 2, x1, y1, BLACK)              # bumper
    cv.rect(x0 + 9, y0 + 2, x1 - 9, y0 + 5, GRAY)   # license plate
    cv.rect(x0 + 10, y0 + 3, x1 - 10, y0 + 4, DARKGRAY)

    # ---- roof: 18w x 2h x 16d at uv(0,76) ----
    roof = box(0, 76, 18, 2, 16)
    x0, y0, x1, y1 = roof["top"]
    cv.rect(x0, y0, x1, y1, RED)
    cv.rect(x0, y0, x1, y0 + 1, RED_DARK)
    cv.rect(x0, y1 - 1, x1, y1, RED_DARK)
    cv.rect(x0, y0, x0 + 1, y1, RED_DARK)
    cv.rect(x1 - 1, y0, x1, y1, RED_DARK)
    cv.rect(x0 + 5, y0 + 5, x1 - 5, y1 - 5, GLASS_DARK)  # sunroof

    x0, y0, x1, y1 = roof["bottom"]
    cv.rect(x0, y0, x1, y1, DARKGRAY)

    for side in ("west", "east", "north", "south"):
        x0, y0, x1, y1 = roof[side]
        cv.rect(x0, y0, x1, y1, RED)
        cv.rect(x0, y0, x1, y0 + 1, RED_LIGHT)

    # ---- left wall: 2w x 6h x 16d at uv(0,96) ----
    left_wall = box(0, 96, 2, 6, 16)
    x0, y0, x1, y1 = left_wall["west"]  # outer face
    cv.rect(x0, y0, x1, y1, RED)
    cv.rect(x0 + 1, y0 + 1, x1 - 1, y0 + 4, GLASS)
    for y in range(y0 + 1, y0 + 4):
        for x in range(x0 + 1, x1 - 1):
            if (x - y) % 4 == 0:
                cv.pixel(x, y, GLASS_LIGHT)
    cv.rect(x0 + 7, y0 + 1, x0 + 9, y0 + 4, RED_DARK)   # pillar
    cv.rect(x0 + 1, y1 - 1, x1 - 1, y1, RED_DARK)       # door line
    for side in ("east", "north", "south", "top", "bottom"):
        cv.rect(*left_wall[side], RED_DARK)

    # ---- right wall: 2w x 6h x 16d at uv(40,96) ----
    right_wall = box(40, 96, 2, 6, 16)
    x0, y0, x1, y1 = right_wall["east"]  # outer face
    cv.rect(x0, y0, x1, y1, RED)
    cv.rect(x0 + 1, y0 + 1, x1 - 1, y0 + 4, GLASS)
    for y in range(y0 + 1, y0 + 4):
        for x in range(x0 + 1, x1 - 1):
            if (x - y) % 4 == 0:
                cv.pixel(x, y, GLASS_LIGHT)
    cv.rect(x0 + 7, y0 + 1, x0 + 9, y0 + 4, RED_DARK)   # pillar
    cv.rect(x0 + 1, y1 - 1, x1 - 1, y1, RED_DARK)       # door line
    for side in ("west", "north", "south", "top", "bottom"):
        cv.rect(*right_wall[side], RED_DARK)

    # ---- back wall: 14w x 6h x 2d at uv(80,96) ----
    back_wall = box(80, 96, 14, 6, 2)
    x0, y0, x1, y1 = back_wall["south"]  # outer face (rear)
    cv.rect(x0, y0, x1, y1, RED)
    glass_with_shine(cv, (x0 + 1, y0 + 1, x1 - 1, y1 - 1))
    for side in ("north", "west", "east", "top", "bottom"):
        cv.rect(*back_wall[side], RED_DARK)

    # ---- wheels: 3w x 8h x 8d at uv(96,48) ----
    wheel = box(96, 48, 3, 8, 8)
    for side in ("west", "east"):
        x0, y0, x1, y1 = wheel[side]
        cx, cy = (x0 + x1) // 2, (y0 + y1) // 2
        cv.rect(x0, y0, x1, y1, TIRE_DARK)
        cv.disc(cx, cy, 3.9, TIRE)
        cv.disc(cx, cy, 1.9, HUB)
        cv.disc(cx, cy, 0.9, HUB_DARK)
        cv.pixel(cx - 3, cy, TIRE_DARK)
        cv.pixel(cx + 3, cy, TIRE_DARK)
    for side in ("north", "south"):
        x0, y0, x1, y1 = wheel[side]
        cv.rect(x0, y0, x1, y1, TIRE_DARK)
        cv.rect(x0 + 1, y0, x0 + 2, y1, TIRE)
    for side in ("top", "bottom"):
        x0, y0, x1, y1 = wheel[side]
        cv.rect(x0, y0, x1, y1, TIRE_DARK)
        cv.rect(x0, y0 + 3, x1, y0 + 5, HUB)

    # ---- lights: 2w x 2h x 1d ----
    for u, front in ((96, HEADLIGHT), (104, TAILLIGHT)):
        light = box(u, 72, 2, 2, 1)
        cv.rect(*light["top"], GRAY_LIGHT)
        cv.rect(*light["bottom"], GRAY)
        cv.rect(*light["west"], GRAY)
        cv.rect(*light["north"], front)
        cv.rect(*light["east"], GRAY)
        cv.rect(*light["south"], GRAY)

    cv.save(os.path.join(OUT, "textures", "entity", theme["name"] + ".png"))


def draw_car_side(cv, ox, oy, s):
    """Side-view car drawn on a 32x32 grid starting at (ox,oy) scaled by s."""

    def r(x0, y0, x1, y1, color):
        cv.rect(int(ox + x0 * s), int(oy + y0 * s), int(ox + x1 * s), int(oy + y1 * s), color)

    def d(cx, cy, rad, color):
        cv.disc(ox + cx * s, oy + cy * s, rad * s, color)

    # shadow
    for i, row in enumerate((24, 25)):
        r(6 - i, row, 26 + i, row + 1, SHADOW)

    # cabin
    r(9, 8, 23, 15, RED)
    r(10, 9, 15, 14, GLASS)
    r(17, 9, 22, 14, GLASS)
    r(15, 9, 17, 14, RED_DARK)
    for x in range(10, 22):
        if (x - 10) % 4 == 0:
            for y in range(9, 14):
                cv.rect(int(ox + x * s), int(oy + y * s), int(ox + x * s + 1), int(oy + y * s + 1), GLASS_LIGHT)
    r(9, 8, 23, 9, RED_LIGHT)

    # body
    r(2, 14, 30, 23, RED)
    r(2, 14, 30, 15, RED_LIGHT)
    r(2, 22, 30, 23, RED_DARK)
    r(3, 17, 29, 19, WHITE_STRIPE)
    r(2, 21, 30, 23, DARKGRAY)
    r(2, 21, 5, 22, GRAY)
    r(27, 21, 30, 22, GRAY)

    # lights
    r(28, 15, 30, 17, HEADLIGHT)
    r(2, 15, 4, 17, TAILLIGHT)

    # wheels
    for cx in (8, 23):
        d(cx, 23, 4.2, TIRE_DARK)
        d(cx, 23, 3.2, TIRE)
        d(cx, 23, 1.7, HUB)
        d(cx, 23, 0.8, HUB_DARK)


def make_remover_item_texture():
    cv = Canvas(16, 16)
    # dark slate backdrop
    cv.rect(1, 1, 15, 15, (36, 38, 42, 255))
    cv.rect(1, 1, 15, 2, (70, 73, 80, 255))
    # simple car silhouette
    cv.rect(3, 5, 13, 11, (158, 162, 168, 255))
    cv.rect(3, 5, 13, 6, (210, 213, 219, 255))
    cv.rect(5, 2, 11, 5, (120, 124, 130, 255))
    cv.rect(6, 3, 8, 5, GLASS_LIGHT)
    cv.rect(8, 3, 10, 5, GLASS_LIGHT)
    cv.disc(4, 11, 2.2, TIRE)
    cv.disc(12, 11, 2.2, TIRE)
    # red diagonal remove slash
    for i in range(8):
        y = 4 + i
        for dx in (-1, 0, 1):
            cv.pixel(12 + dx, y, (210, 40, 40, 255))
            cv.pixel(7 + dx, y, (210, 40, 40, 255))
    cv.save(os.path.join(OUT, "textures", "item", "car_remover.png"))


def make_icon():
    small = Canvas(32, 32)
    draw_car_side(small, 0, 0, 1)
    big = Canvas(128, 128)
    for y in range(32):
        for x in range(32):
            big.rect(x * 4, y * 4, x * 4 + 4, y * 4 + 4, small.px[y][x])
    big.save(os.path.join(OUT, "icon.png"))


def make_item_texture(theme):
    RED = theme["RED"]
    RED_DARK = theme["RED_DARK"]
    RED_LIGHT = theme["RED_LIGHT"]
    cv = Canvas(16, 16)

    # ground shadow
    cv.rect(3, 14, 13, 15, SHADOW)

    # cabin
    cv.rect(4, 2, 12, 6, RED)
    cv.rect(5, 3, 7, 5, GLASS)
    cv.rect(8, 3, 11, 5, GLASS)
    cv.rect(7, 3, 8, 5, RED_DARK)
    cv.pixel(5, 3, GLASS_LIGHT)
    cv.pixel(8, 3, GLASS_LIGHT)
    cv.rect(4, 2, 12, 3, RED_LIGHT)

    # body
    cv.rect(1, 6, 15, 11, RED)
    cv.rect(1, 6, 15, 7, RED_LIGHT)
    cv.rect(1, 10, 15, 11, RED_DARK)
    cv.rect(2, 8, 14, 9, WHITE_STRIPE)

    # lights
    cv.rect(13, 7, 15, 8, HEADLIGHT)
    cv.rect(1, 7, 2, 8, TAILLIGHT)

    # wheels
    for cx in (4, 11):
        cv.disc(cx, 11, 2.7, TIRE_DARK)
        cv.disc(cx, 11, 2.0, TIRE)
        cv.disc(cx, 11, 1.0, HUB)
        cv.pixel(cx, 11, HUB_DARK)

    cv.save(os.path.join(OUT, "textures", "item", theme["name"] + ".png"))


if __name__ == "__main__":
    for key in THEMES:
        make_entity_texture(THEMES[key])
        make_item_texture(THEMES[key])
    make_remover_item_texture()
    make_icon()
