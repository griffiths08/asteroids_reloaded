void Draw_GUI() {
  hud.beginDraw();
  hud.background(0, 0);
  hud.fill(0, 200);
  hud.stroke(255, 0);
  hud.rect(0, 0, size.x, 100);
  hud.fill(0, 100);
  hud.stroke(255, 200);
  hud.fill(0, 200);
  if (Game_Mode!=1) hud.rect(50, 20, 200, 20);
  if (Game_Mode==1) hud.rect(50, 20, Players.get(0).Health, 20);
  if (Game_Mode==1) hud.rect(50, 60, Players.get(1).Health, 20);
  hud.stroke(255, 255);
  hud.fill(#0FFFF1, 200);
  hud.rect(50, 20, Players.get(0).Health, 6);
  hud.fill(#FF76F6, 200);
  hud.rect(50, 20+6, Players.get(0).Health, 6);
  hud.fill(#FAE923, 200);
  hud.rect(50, 20+12, Players.get(0).Health, 6);
  if (Game_Mode==1) {
    hud.fill(#0FFFF1, 200);
    hud.rect(50, 60, Players.get(1).Health, 6);
    hud.fill(#FF76F6, 200);
    hud.rect(50, 60+6, Players.get(1).Health, 6);
    hud.fill(#FAE923, 200);
    hud.rect(50, 60+12, Players.get(1).Health, 6);
  }
  hud.textFont(Font1, 40);
  hud.fill(255);
  hud.stroke(255);
  if (Game_Mode!=1) hud.text(str(int(Players.get(0).Health/2))+'%', 20+Players.get(0).Health+5, 75);
  if (Game_Mode==1) hud.text(str(int(Players.get(0).Health/2))+'%', 60+Players.get(0).Health+5, 45);
  if (Game_Mode==1) hud.text(str(int(Players.get(1).Health/2))+'%', 60+Players.get(1).Health+5, 85);
  hud.textFont(Font1);
  hud.text("P1 :"+str(Players.get(0).Score), 400, 50);
  if (Game_Mode==1)hud.text("P2 :"+str(Players.get(1).Score), 400, 90);
  if (Game_Mode==2)hud.text("Time :"+str(int((Time-frameCount)/.6)), 400, 90);
  hud.text("High Score", 720, 50);
  hud.text(str(High_Score), 720, 90);
  if (Paused&&Dialog_Boxs.size()==0) new Dialog(new PVector(size.x/2, size.y/2), 5).Update();
  for (int i = Dialog_Boxs.size()-1; i >= 0; i--) { 
    Dialog_Boxs.get(i).Update();
    if (Dialog_Boxs.get(i).dis) Dialog_Boxs.remove(i);
  }
  hud.endDraw();
}