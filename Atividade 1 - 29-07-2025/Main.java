import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in); // criar scanner
        System.out.println("Preencha os dados do produto:");
        System.out.print("Nome: ");
        String nome = input.nextLine(); // receber nome
        System.out.print("Preço: ");
        double preco = Double.parseDouble(input.nextLine()); // recebe valor com "."
        System.out.print("Quantidade em estoque: ");
        int quantidade = input.nextInt(); // receber quantidade de tal produto

        Product produto = new Product(nome, preco, quantidade); // criar produto
        System.out.printf("\nDados do produto: %s\n", produto.Text());

        System.out.print("\nDiga quantos produtos deseja adicionar ao estoque: ");
        produto.AddProducts(input.nextInt()); // adicionar x quantidade de produtos
        System.out.printf("\nDados atualizados: %s\n", produto.Text());

        System.out.print("\nDiga quantos produtos deseja remover do estoque: ");
        produto.RemoveProducts(input.nextInt()); // remover x quantidade de produtos
        System.out.printf("\nDados atualizados: %s\n", produto.Text());
        input.close();
    }
}
