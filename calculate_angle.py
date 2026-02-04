import math
from dataclasses import dataclass


@dataclass
class Point:
    x: float
    y: float


BLUE_GOAL = Point(0,   144)


def main():
    print("Enter bot pose for the BLUE alliance")
    bot_x = int(input("Bot x: "))
    bot_y = int(input("Boy y: "))
    bot = Point(bot_x, bot_y)

    blue_angle = 180 - math.degrees(calculate_angle(BLUE_GOAL, bot))
    print(f"Angle: {round(blue_angle, 0)}")


def calculate_angle(goal: Point, bot: Point) -> float:
    opp = abs(goal.y - bot.y)
    adj = abs(goal.x - bot.x)
    return math.atan(opp / adj)


if __name__ == "__main__":
    main()
