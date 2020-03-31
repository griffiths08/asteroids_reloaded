void Draw_Background() {//Background
  background(0);
  if (frameCount % 2 == 0) {
    pg.beginDraw();
    Galaxy.set("time", millis() / 1000.0); // Set Shader Time
    pg.shader(Galaxy); // Set Shader
    pg.rect(0, 0, size.x, size.y); // Draw Backround
    pg.resetShader(); // Reset Shader
    pg.endDraw();
    bt = true;
  }
}
void Render_Playfield() {
  Playfield.beginDraw();
  Playfield.background(0, 0);
  Playfield.shader(Grid_Generator); // Set Shader
  Playfield.rect(0, 0, size.x*2, size.y*2);
  Playfield.resetShader();
  Update_Particle_Effects();
  Update_Players();
  Update_Asteroids();
  Update_Projectiles();
  Playfield.endDraw();
}
void Render_Screen() {
  out.beginDraw();
  out.background(0, 0);
  out.fill(0, 0);
  out.translate(Players.get(0).Location.x, Players.get(0).Location.y);
  out.translate(-Players.get(0).Location.x, -Players.get(0).Location.y);
  out.translate(size.x, size.y, -200);
  if (Game_Mode==1) {
  } else {
    out.rotateY(radians(map(Players.get(0).Location.x, 0, size.x*2, -10, 10)));
    out.rotateX(radians(map(Players.get(0).Location.y, 0, size.y*2, 10, -10)));
  }
  out.rect(-size.x, -size.y, size.x*2, size.y*2);
  out.image(Playfield, -size.x, -size.y, size.x*2, size.y*2);
  out.fill(0, 150);
  if (Game_Mode==0) {
    out.rect(-size.x-(size.x*0.05*sin(radians(bounds)))-(size.x*0.05*2), -size.y, size.x*0.05, size.y*2);
    out.rect(size.x+(size.x*0.05)*sin(radians(bounds))+(size.x*0.05), -size.y, size.x*0.05, size.y*2);
    out.rect(-size.x, -size.y-(size.y*0.05*sin(radians(bounds)))-(size.y*0.05*2), size.x*2, size.y*0.05);
    out.rect(-size.x, size.y+(size.y*0.05*sin(radians(bounds)))+(size.y*0.05), size.x*2, size.y*0.05);
    out.rect(-size.x-(size.x*0.05*sin(radians(bounds)))-(size.x*0.05*2), -size.y-(size.y*0.05*sin(radians(bounds)))-(size.y*0.05*2), size.x*0.05, size.y*0.05);
    out.rect(size.x+(size.x*0.05)*sin(radians(bounds))+(size.x*0.05), -size.y-(size.y*0.05*sin(radians(bounds)))-(size.y*0.05*2), size.x*0.05, size.y*0.05);
    out.rect(-size.x-(size.x*0.05*sin(radians(bounds)))-(size.x*0.05*2), size.y+(size.y*0.05*sin(radians(bounds)))+(size.y*0.05), size.x*0.05, size.y*0.05);
    out.rect(size.x+(size.x*0.05)*sin(radians(bounds))+(size.x*0.05), size.y+(size.y*0.05*sin(radians(bounds)))+(size.y*0.05), size.x*0.05, size.y*0.05);
  }
  bounds += 4;
  out.camera();
  Draw_GUI();

  if (Game_Mode==1) {
    PVector fp= PVector.lerp(Players.get(0).Location, Players.get(1).Location, 0.5);
    out.camera(fp.x, fp.y, ((size.y+Players.get(0).Location.dist(Players.get(1).Location)*0.7)/2.0) / tan(PI*30.0 / 180.0)-cam, fp.x, fp.y, -50, 0, 1, 0);
  } else
    out.camera(Players.get(0).Location.x, Players.get(0).Location.y, (size.y/2.0) / tan(PI*30.0 / 180.0)-cam, Players.get(0).Location.x, Players.get(0).Location.y, -50, 0, 1, 0);
  out.endDraw();
}
void Render_Output() {
  fill(0, 0);
  if (bt)image(pg, 0, 0, size.x*Scale.x, size.y*Scale.y);
  Scanline_Generator.set("tex0", out);
  shader(Scanline_Generator);
  rect(0, 0, size.x*Scale.x, size.y*Scale.y);
  resetShader();
  Scanline_Generator.set("tex0", hud);
  shader(Scanline_Generator);
  rect(0, 0, size.x*Scale.x, size.y*Scale.y);
  resetShader();
}