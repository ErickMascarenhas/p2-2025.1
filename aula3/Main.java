import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        System.out.print("Preencha os dados do produto:\nNome: ");
        String nome = input.nextLine();
        System.out.print("Preço: ");
        double preco = Double.parseDouble(input.nextLine());
        System.out.print("Quantidade em estoque: ");
        int quantidade = input.nextInt();
        System.out.printf("\nDados do produto: %s, $ %.2f, %d unidades, Total: $ %.2f\n", nome, preco, quantidade, preco * quantidade);
        System.out.print("\nDiga quantos produtos deseja adicionar ao estoque: ");
        quantidade += input.nextInt();
        System.out.printf("\nDados atualizados: %s, $ %.2f, %d unidades, Total: $ %.2f\n", nome, preco, quantidade, preco * quantidade);
        System.out.print("\nDiga quantos produtos deseja remover do estoque: ");
        quantidade -= input.nextInt();
        System.out.printf("\nDados atualizados: %s, $ %.2f, %d unidades, Total: $ %.2f\n", nome, preco, quantidade, preco * quantidade);
    }
}