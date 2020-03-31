void Update_Particle_Effects() {
  for (int b = Particle_Systems.size()-1; b >= 0; b--) { 
    Particle_Systems.get(b).Update();
    if (Particle_Systems.get(b).Particles.size() ==0)Particle_Systems.remove(b);
  }
  for (int b = ex.size()-1; b >= 0; b--) { 
    ex.get(b).Update();
    if (ex.get(b).isDead())ex.remove(b);
  }
}
void PFX1(PVector L) {
  Particle_System P = new Particle_System(L, 0);
  for (int i=0; i<40; i++) P.addParticle();
  Particle_Systems.add(P);
}
void PFX2(PVector L) {
  Particle_System P = new Particle_System(L, 1);
  for (int i=0; i<10; i++) P.addParticle();
  Particle_Systems.add(P);
}

void EX1(PVector L, float ts) {
  ex.add(new exp(L, ts));
}
class exp {

  PVector Location;
  float Size, tsize;
  exp(PVector L, float ts) {
    Location =L.copy();
    Size=1;
    tsize=ts;

    if (Sound) n1.amp(0.4);
  }
  void Update() {
    // color co = color(int(random(60, 255)), int(random(60, 255)), int(random(60, 255)));
    // stroke(255, 100);
    //shader(FireShader); // Set Shader
    //   rect(0, 0, float(width), float(height)); // Draw Backround
    Playfield.ellipse(Location.x, Location.y, Size, Size);
    color co=#FFCC00;
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
    //resetShader(); // Reset Shader
    Playfield.fill(co, 100);
    Playfield.stroke(255, 100);
    Playfield.strokeWeight(4);
    Playfield.ellipse(Location.x, Location.y, Size, Size);
    Playfield.strokeWeight(1);
    //    fill(co, 100);
    if (!Paused) Size+=16;
  }
  boolean isDead() {
    if (Size>tsize) {
      if (Sound) n1.amp(0);
      return true;
    } else {
      return false;
    }
  }
}