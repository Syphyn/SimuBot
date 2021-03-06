package Triton.Shape;

import Proto.RemoteCommands;

public class Vec2D {
    public double x, y;
    private String name;
    
    public Vec2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D(Vec2D target) {
        this(target.x, target.y);
    }

    public Vec2D add(Vec2D toAdd) {
        return new Vec2D(this.x + toAdd.x, this.y + toAdd.y);
    }
    
    public Vec2D add(double x, double y) {
        return new Vec2D(this.x + x, this.y + y);
    }

    public Vec2D sub(Vec2D toSubtract) {
        return new Vec2D(this.x - toSubtract.x, this.y - toSubtract.y);
    }

    public Vec2D mult(double z) {
        return new Vec2D(this.x * z, this.y * z);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public double getAngle() {
        return Math.atan(this.y / this.x);
    }

    public Vec2D rotate(double angle) {
        double newX = Math.cos(angle) * x + -Math.sin(angle) * y;
        double newY = Math.sin(angle) * x + Math.cos(angle) * y;
        return new Vec2D(newX, newY);
    }

    public double mag() {
        return Math.pow(Math.pow(x, 2) + Math.pow(y, 2), 0.5);
    }

    public Vec2D norm() {
        double mag = mag();
        return new Vec2D(x / mag, y / mag);
    }

    public static double dist(Vec2D v1, Vec2D v2) {
        double diffX2 = Math.pow((v2.x - v1.x), 2);
        double diffY2 = Math.pow((v2.y - v1.y), 2);
        return Math.pow(diffX2 + diffY2, 0.5);
    }

    public static double dist2(Vec2D v1, Vec2D v2) {
        double diffX2 = Math.pow((v2.x - v1.x), 2);
        double diffY2 = Math.pow((v2.y - v1.y), 2);
        return diffX2 + diffY2;
    }

    @Override
    public String toString() {
        String s = "";
        s += "<" + x + ", " + y + ">";
        return s;
    }

    public RemoteCommands.Vec2D toProto() {
        RemoteCommands.Vec2D.Builder builder = RemoteCommands.Vec2D.newBuilder();
        builder.setX(x);
        builder.setY(y);
        return builder.build();
    }
}