import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestStream {

    public static void main(String[] args){
        Integer max = 1000;
        Integer sum = 0;

        Random random = new Random();
        ArrayList<String> strs = new ArrayList<>();


        for(int i = 0;i<=1000;i++){
            strs.add(String.format("%d",random.nextInt(max)));
        }

        List<Integer> collect = Stream.of(strs.toArray()).map(str -> Integer.parseInt(String.valueOf(str))).filter(num -> num > max / 2).collect(Collectors.toList());


        for(int i=0;i<collect.size();i++){
            sum += collect.get(i);
        }
        sum /= collect.size();

        System.out.println(collect);
        System.out.println(collect.size());
        System.out.println(sum);


    }
}
