class Particle_System {
  ArrayList<Particle> Particles;
  PVector Origin;
  int Type;
  Particle_System(PVector Location, int T) {
    Origin = Location.copy();
    Particles = new ArrayList<Particle>();
    Type = T;
  }
  void addParticle() {
    Particles.add(new Particle(Origin, Type));
  }
  void addParticle(float Angle) {
    Particle p = new Particle(Origin, Type);
    p.Velocity = PVector.fromAngle(radians(Angle)).mult(10);
    Particles.add(p);
  }
  void Update() {
    for (int i = Particles.size()-1; i >= 0; i--) {
      Particle P = Particles.get(i);
      if (P.isDead()) Particles.remove(i);
      else P.update();
    }
  }
}

class Particle extends Physics_Object {
  float lifespan, Size, Rot;

  int Type;
  color co=#FFCC00;
  Particle(PVector L, int T) {
    Is_Game_Obj= true;
    switch(int(random(0, 3))) {
    case 0: 
      co = #0FFFF1;
      break;
    case 1: 
      co = #FF76F6;
      break;
    case 2: 
      co = #FAE923;
      break;
    }
    Type = T;
    Acceleration = new PVector(0, 0);
    if (Type==1) {
      Velocity = new PVector(random(-1, 1), random(1, 2));
    } else if (Type==2) {
      Velocity = new PVector(random(-4, 4), random(-4, 4));
    } else {
      Velocity = new PVector(random(-4, 4), random(-4, 4));
    }
    Location = L.copy();
    if (Type==2)  lifespan = random(40);
    else lifespan = random(100);
    Size = random(4);
    Rot = random(0, 361);
    Rot_Dir = random(-11.4592*2, 11.4592*2);
    Wrap_Offset=0;
  }
  void update() {
    lifespan -= 1.0;
    Move();
    Render();
  }
  void Render() {
    stroke(0, 0, 0, 0);
    if (Type==1) {
      translate(Location.x, Location.y);
      rotate(radians(Rot));
      stroke(255, 0, 0, lifespan*2.55);
      fill(255, 0, 0, lifespan*2.55);
      rect(-(Size/2), -(Size/2), Size, Size);
      rotate(radians(-Rot));
      translate(-Location.x, -Location.y);
    } else if (Type==2) {
      Playfield.translate(Location.x, Location.y);
      Playfield.rotate(radians(Rot));
      Playfield.stroke(co, lifespan*6.375);
      Playfield.fill(co, lifespan*6.375);
      Playfield.rect(-(Size/2), -(Size/2), Size, Size);
      Playfield.rotate(radians(-Rot));
      Playfield.translate(-Location.x, -Location.y);
    } else {
      Playfield.translate(Location.x, Location.y);
      Playfield.rotate(radians(Rot));
      Playfield.stroke(co, lifespan*2.55);
      Playfield.fill(co, lifespan*2.55);
      Playfield.rect(-(Size/2), -(Size/2), Size, Size);
      Playfield.rotate(radians(-Rot));
      Playfield.translate(-Location.x, -Location.y);
    }
  }

  boolean isDead() {
    if (lifespan < 0.0) {
      return true;
    } else {
      return false;
    }
  }
}