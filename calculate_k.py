import matplotlib.pyplot as plt
import numpy as np
import math


# (desired distance, actual distance)
DATA = [
    ( 4.00, 0.58),
    ( 6.00, 0.98),
    ( 8.00, 1.36),
    (10.00, 1.66),
    (12.00, 2.05),
    (14.00, 2.37),
    (16.00, 2.65),
    (20.00, 3.29),
    (24.00, 3.92),
]

TARGET_HEIGHT = 0
BOT_HEIGHT = 0.36
LAUNCH_ANGLE = math.radians(60)
STANDARD_GRAVITY = 9.80665


def main():
    x_values = [calculate_velocity(pair[1]) for pair in DATA]
    y_values = [calculate_velocity(pair[0]) for pair in DATA]

    (gradient, intercept) = np.polyfit(np.array(x_values), np.array(y_values), 1)
    line_y_values = [gradient*x + intercept for x in x_values]

    k = round(gradient, 2)
    c = round(intercept, 2)
    results = f"k = {k} and c = {c}"
    
    print(results)
    plt.title(results)
    plt.xlabel("Actual velocity")
    plt.ylabel("Desired velocity")
    plt.scatter(x_values, y_values, color="blue")
    plt.plot(x_values, line_y_values, color="red")
    plt.legend()
    plt.grid()
    plt.show()


def calculate_velocity(distance):
    height_diff = TARGET_HEIGHT - BOT_HEIGHT
    cos = math.cos(LAUNCH_ANGLE)
    tan = math.tan(LAUNCH_ANGLE)

    denominator = (2*cos*cos)*(distance*tan - height_diff)
    if denominator <= 0:
        raise ValueError("Invalid distance: Impossible to shoot from this distance: Cannot calculate velocity")

    inner = distance*distance*STANDARD_GRAVITY / denominator
    velocity = math.sqrt(inner)

    return velocity


if __name__ == "__main__":
    main()
