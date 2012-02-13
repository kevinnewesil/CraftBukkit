package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.TrigMath;
// CraftBukkit end

public class ControllerLook {

    private EntityLiving a;
    private float b;
    private float c;
    private boolean d = false;
    private double e;
    private double f;
    private double g;

    public ControllerLook(EntityLiving entityliving) {
        this.a = entityliving;
    }

    public void a(Entity entity, float f, float f1) {
        this.e = entity.locX;
        if (entity instanceof EntityLiving) {
            this.f = entity.locY + (double) ((EntityLiving) entity).y();
        } else {
            this.f = (entity.boundingBox.b + entity.boundingBox.e) / 2.0D;
        }

        this.g = entity.locZ;
        this.b = f;
        this.c = f1;
        this.d = true;
    }

    public void a(double d0, double d1, double d2, float f, float f1) {
        this.e = d0;
        this.f = d1;
        this.g = d2;
        this.b = f;
        this.c = f1;
        this.d = true;
    }

    public void a() {
        this.a.pitch = 0.0F;
        if (this.d) {
            this.d = false;
            double d0 = this.e - this.a.locX;
            double d1 = this.f - (this.a.locY + (double) this.a.y());
            double d2 = this.g - this.a.locZ;
            double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (TrigMath.atan2(d2, d0) * 180.0D / 3.1415927410125732D) - 90.0F; // CraftBukkit - Use TrigMath.atan2 instead of Math.atan2
            float f1 = (float) (-(TrigMath.atan2(d1, d3) * 180.0D / 3.1415927410125732D)); // CraftBukkit - Use TrigMath.atan2 instead of Math.atan2

            this.a.pitch = this.a(this.a.pitch, f1, this.c);
            this.a.yaw = this.a(this.a.yaw, f, this.b);
        }
    }

    private float a(float f, float f1, float f2) {
        float f3;

        for (f3 = f1 - f; f3 < -180.0F; f3 += 360.0F) {
            ;
        }

        while (f3 >= 180.0F) {
            f3 -= 360.0F;
        }

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }
}
