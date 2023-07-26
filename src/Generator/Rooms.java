package Generator;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.ArrayList;
import java.util.Random;

public class Rooms {
    private static Rooms instance = new Rooms();
    private Rooms(){}
    public static Rooms getInstance() {
        return instance;
    }

    Random random = new Random();
    SimplexNoiseGenerator simplex = new SimplexNoiseGenerator(random);
    ArrayList<Material[]>textures = new ArrayList<>();

    public void registerTextures(){
        Material[]texture0 ={Material.DIRT,Material.COARSE_DIRT,Material.GRASS_BLOCK,Material.MOSS_BLOCK};
        Material[]texture1 ={Material.COBBLESTONE,Material.MOSSY_COBBLESTONE,Material.CRACKED_STONE_BRICKS,Material.MOSSY_STONE_BRICKS,Material.STONE_BRICKS,Material.SMOOTH_STONE};
        Material[]texture2 ={Material.SANDSTONE,Material.SMOOTH_SANDSTONE,Material.CHISELED_SANDSTONE,Material.CUT_SANDSTONE};
        Material[]texture3 ={Material.NETHERRACK,Material.CRIMSON_NYLIUM,Material.NETHER_QUARTZ_ORE,Material.WARPED_NYLIUM,Material.BLACKSTONE,Material.GILDED_BLACKSTONE};
        textures.add(texture0);
        textures.add(texture1);
        textures.add(texture2);
        textures.add(texture3);
    }

    public int compare(int division,double randDouble) {
        double step =1.0 / division;
        int count = 0;
        for (double d = 0; d <= 1; d += step) {
            if (randDouble < d) {
                return count;
            }
            count += 1;
        }
        return -1;
    }

    public void clearRoom(Location location,int size){
        World w = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        for (int a = x;a < x + size;a++){
            for (int b = y - 10;b < y + 10;b++){
                for (int c = z;c < z + size;c ++){
                    Block block = w.getBlockAt(a,b,c);
                    block.setType(Material.AIR);
                }
            }
        }
    }
    public void generateRoom(Location location,int roomSize,int type){
        if(type > textures.size() || type < 0)
            type = 0;
        World w = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        clearRoom(location,roomSize);
        Material[]materials = textures.get(type);
        for(int a = x;a < x + roomSize;a++){
            for (int b = y - 1;b <= y + 8;b++){
                for(int c = z;c < z + roomSize;c++){
                    Block block = w.getBlockAt(a,b,c);
                    if(block.getType() == Material.CHEST)continue;
                    double offsetA = a + random.nextDouble();
                    double offsetC = c + random.nextDouble();
                    double noise1 = simplex.noise(offsetA / 100.0, offsetC / 100.0);
                    double noise2 = simplex.noise(offsetA / 50.0, offsetC / 50.0);
                    double noise3 = simplex.noise(offsetA / 25.0, offsetC / 25.0);
                    double noise = (noise1 + noise2 * 0.5 + noise3 * 0.25);
                    if(b == y + 8) {
                        Block floor = w.getBlockAt(a, b, c);
                        floor.setType(Material.BARRIER);
                    }
                    if(b <= y){
                        Block floor = w.getBlockAt(a,b,c);
                        double absNoise =Math.sin(Math.abs(noise));
                        int id = compare(materials.length,absNoise);
                        if(id == -1){
                            floor.setType(materials[0]);
                            continue;
                        }
                        floor.setType(materials[id - 1]);
                    }else {
                        if(a == x || c == z || a == x + roomSize - 1 || c == z + roomSize - 1) {
                            Block wall = w.getBlockAt(a, b, c);
                            double absNoise =Math.sin(Math.abs(noise));
                            int id = compare(materials.length, absNoise);
                            if (id == -1) {
                                wall.setType(materials[0]);
                                continue;
                            }
                            if (type == 0) {
                                wall.setType(textures.get(1)[id]);
                            } else {
                                wall.setType(materials[id - 1]);
                            }
                        }
                    }
                }
            }
        }
    }
}
