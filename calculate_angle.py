import math


GOAL_X = 0
GOAL_Y = 144


def main():
    bot_x = int(input("Bot x: "))
    bot_y = int(input("Boy y: "))
    angle = math.degrees(calculate_angle(bot_x, bot_y))
    print(f"Angle: {angle}")


def calculate_angle(bot_x, bot_y):
    x_diff = GOAL_X - bot_x
    y_diff = GOAL_Y - bot_y
    denominator = math.sqrt(x_diff*x_diff + y_diff*y_diff)
    return math.radians(180) - math.asin(abs(x_diff) / denominator)


if __name__ == "__main__":
    main()
