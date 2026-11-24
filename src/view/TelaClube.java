package view;

import controller.GerenciadorClube;
import exception.CadastroInvalidoException;
import exception.PontuacaoInsuficienteException;
import model.Beneficio;
import model.MembroClube;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class TelaClube extends JFrame {
    private static final long serialVersionUID = 1L;
    private final transient GerenciadorClube gerenciador = new GerenciadorClube();
    private final JTextField nome = new JTextField(20);
    private final JTextField email = new JTextField(20);
    private final JComboBox<String> plano = new JComboBox<>(new String[]{"Essencial · 1x", "Premium · 2x"});
    private final JTextField pesquisa = new JTextField(20);
    private final JTextField id = new JTextField(8);
    private final JTextField pontos = new JTextField(8);
    private final JComboBox<Beneficio> beneficio = new JComboBox<>(Beneficio.values());
    private final JTextArea extrato = new JTextArea(9, 40);
    private final JLabel resumo = new JLabel();
    private final JLabel ordem = new JLabel("Ordenação: ID crescente");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nome", "E-mail", "Plano", "Pontos"}, 0) {
        private static final long serialVersionUID = 1L;
        @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        @Override public Class<?> getColumnClass(int coluna) {
            return coluna == 0 || coluna == 4 ? Integer.class : String.class;
        }
    };
    private final JTable tabela = new JTable(modelo);
    private boolean ranking;

    public TelaClube() {
        super("Clube Vantagens | Fidelidade em memória");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 740));
        setSize(1120, 820);
        setLocationRelativeTo(null);
        JPanel raiz = new JPanel(new BorderLayout(16, 16));
        raiz.setBorder(new EmptyBorder(20, 24, 16, 24));
        raiz.setBackground(new Color(242, 245, 248));
        setContentPane(raiz);

        JPanel cabecalho = new JPanel(new BorderLayout(8, 8));
        cabecalho.setOpaque(false);
        JLabel titulo = new JLabel("CLUBE VANTAGENS");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 27));
        titulo.setForeground(new Color(27, 67, 81));
        cabecalho.add(titulo, BorderLayout.NORTH);
        cabecalho.add(new JLabel("Cadastre membros, reconheça a fidelidade e transforme pontos em benefícios."), BorderLayout.CENTER);
        resumo.setBorder(new EmptyBorder(6, 0, 0, 0));
        cabecalho.add(resumo, BorderLayout.SOUTH);
        raiz.add(cabecalho, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setOpaque(false);
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("01  Adesão ao clube", criarCadastro());
        abas.addTab("02  Pontos e benefícios", criarOperacoes());
        centro.add(abas, BorderLayout.NORTH);
        centro.add(criarListagem(), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        rodape.add(new JLabel("Dados somente em RAM. Ao sair, todos os cadastros e pontos serão perdidos."), BorderLayout.CENTER);
        rodape.add(botao("Sair", this::solicitarSaida), BorderLayout.EAST);
        raiz.add(rodape, BorderLayout.SOUTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(29);
        tabela.getTableHeader().setReorderingAllowed(false);
        tabela.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                id.setText(modelo.getValueAt(tabela.getSelectedRow(), 0).toString());
                executar(this::mostrarExtrato);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent evento) { solicitarSaida(); }
        });
        atualizarListagem();
    }

    private JPanel criarCadastro() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new EmptyBorder(14, 12, 14, 12));
        campo(painel, "Nome completo", nome, 0);
        campo(painel, "E-mail (único)", email, 1);
        campo(painel, "Plano de fidelidade", plano, 2);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2; c.gridy = 0; c.gridheight = 3;
        c.insets = new Insets(6, 24, 6, 6);
        painel.add(botao("Cadastrar", () -> executar(this::cadastrar)), c);
        return painel;
    }

    private JPanel criarOperacoes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new EmptyBorder(14, 12, 14, 12));
        campo(painel, "ID do membro", id, 0);
        campo(painel, "Pontos-base (inteiro positivo)", pontos, 1);
        campo(painel, "Benefício para resgate", beneficio, 2);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2; c.insets = new Insets(3, 20, 3, 6); c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        painel.add(botao("Consultar extrato", () -> executar(this::mostrarExtrato)), c);
        c.gridy = 1;
        painel.add(botao("Acumular pontos", () -> executar(this::acumular)), c);
        c.gridy = 2;
        painel.add(botao("Resgatar benefício", () -> executar(this::resgatar)), c);
        return painel;
    }

    private JPanel criarListagem() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JLabel rotulo = new JLabel("Nome, e-mail ou ID:");
        rotulo.setLabelFor(pesquisa);
        busca.add(rotulo);
        busca.add(pesquisa);
        busca.add(botao("Pesquisar", this::atualizarListagem));
        busca.add(botao("Mostrar todos", () -> { pesquisa.setText(""); atualizarListagem(); }));
        busca.add(botao("Ordenar por ID", () -> { ranking = false; atualizarListagem(); }));
        busca.add(botao("Ranking", () -> { ranking = true; atualizarListagem(); }));
        pesquisa.addActionListener(evento -> atualizarListagem());
        painel.add(busca, BorderLayout.NORTH);

        JPanel listagem = new JPanel(new BorderLayout(6, 6));
        ordem.setBorder(new EmptyBorder(0, 10, 0, 0));
        listagem.add(ordem, BorderLayout.NORTH);
        JScrollPane rolagemTabela = new JScrollPane(tabela);
        rolagemTabela.setPreferredSize(new Dimension(900, 160));
        listagem.add(rolagemTabela, BorderLayout.CENTER);

        extrato.setEditable(false);
        extrato.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        extrato.setLineWrap(true);
        extrato.setWrapStyleWord(true);
        extrato.setText("Selecione um membro na listagem ou consulte pelo ID na aba Pontos e benefícios.");
        JScrollPane rolagemExtrato = new JScrollPane(extrato);
        rolagemExtrato.setBorder(BorderFactory.createTitledBorder("Extrato do membro · movimentações desta execução"));
        JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listagem, rolagemExtrato);
        divisao.setResizeWeight(0.55);
        painel.add(divisao, BorderLayout.CENTER);
        return painel;
    }

    private void campo(JPanel painel, String texto, JComponent componente, int linha) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = linha; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(3, 6, 3, 12);
        JLabel rotulo = new JLabel(texto);
        rotulo.setLabelFor(componente);
        painel.add(rotulo, c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        painel.add(componente, c);
    }

    private JButton botao(String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.addActionListener(evento -> acao.run());
        return botao;
    }

    private void cadastrar() throws CadastroInvalidoException {
        MembroClube membro = gerenciador.cadastrar(nome.getText(), email.getText(), plano.getSelectedIndex() == 1);
        nome.setText(""); email.setText("");
        id.setText(String.valueOf(membro.getId()));
        pesquisa.setText("");
        atualizarListagem();
        mostrarExtrato();
        informar("Membro #" + membro.getId() + " cadastrado com sucesso.");
    }

    private int lerId() { return Integer.parseInt(id.getText().trim()); }

    private void acumular() throws CadastroInvalidoException {
        int codigo = lerId();
        int quantidade = Integer.parseInt(pontos.getText().trim());
        gerenciador.acumular(codigo, quantidade);
        pontos.setText("");
        atualizarListagem();
        mostrarExtrato();
        informar("Pontos creditados conforme o plano do membro.");
    }

    private void resgatar() throws CadastroInvalidoException, PontuacaoInsuficienteException {
        gerenciador.resgatar(lerId(), (Beneficio) beneficio.getSelectedItem());
        atualizarListagem();
        mostrarExtrato();
        informar("Benefício resgatado com sucesso.");
    }

    private void mostrarExtrato() throws CadastroInvalidoException {
        extrato.setText(gerenciador.emitirExtrato(lerId()));
        extrato.setCaretPosition(0);
    }

    private void atualizarListagem() {
        modelo.setRowCount(0);
        for (MembroClube membro : gerenciador.pesquisar(pesquisa.getText(), ranking)) {
            modelo.addRow(new Object[]{membro.getId(), membro.getNome(), membro.getEmail(), membro.getPlano(), membro.getPontos()});
        }
        resumo.setText(gerenciador.getQuantidadeMembros() + " membros  |  " + gerenciador.getTotalPontos()
                + " pontos em circulação  |  Essencial: 1x  ·  Premium: 2x");
        ordem.setText((ranking ? "Ranking: maior saldo primeiro; empate por ID" : "Ordenação: ID crescente")
                + "  |  " + modelo.getRowCount() + " resultado(s)");
    }

    @FunctionalInterface
    private interface AcaoTela {
        void executar() throws CadastroInvalidoException, PontuacaoInsuficienteException;
    }

    private void executar(AcaoTela acao) {
        try {
            acao.executar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe números inteiros válidos para ID e pontos (até 2147483647).",
                    "Valor numérico inválido", JOptionPane.WARNING_MESSAGE);
        } catch (CadastroInvalidoException | PontuacaoInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operação não realizada", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void informar(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Clube Vantagens", JOptionPane.INFORMATION_MESSAGE);
    }

    private void solicitarSaida() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja sair? Todos os dados desta execução serão perdidos.", "Encerrar clube",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) dispose();
    }
}
