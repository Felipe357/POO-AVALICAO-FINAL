package view;

import controller.GerenciadorClube;
import exception.CadastroInvalidoException;
import exception.PontuacaoInsuficienteException;
import model.Beneficio;
import model.MembroClube;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class TelaClube extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color FUNDO = new Color(241, 245, 249);
    private static final Color TEXTO = new Color(30, 41, 59);
    private static final Color AZUL = new Color(29, 78, 149);
    private static final Color VERDE = new Color(22, 101, 65);
    private static final Color ROXO = new Color(104, 56, 153);
    private static final Color AMBAR = new Color(133, 77, 14);
    private static final Color BORDA = new Color(218, 226, 235);
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
    private final JLabel retorno = new JLabel("Tudo pronto. Cadastre um membro para começar.");
    private final JLabel membroConsultado = new JLabel("Selecione um membro para consultar o extrato.");
    private final JLabel saldoBeneficio = new JLabel("Consulte um membro para conferir o saldo do benefício.");
    private final JLabel ordem = new JLabel("Ordenação: ID crescente");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nome", "E-mail", "Plano", "Pontos", "Situação do saldo"}, 0) {
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
        setMinimumSize(new Dimension(1080, 800));
        setSize(1180, 900);
        setLocationRelativeTo(null);
        JPanel raiz = new JPanel(new BorderLayout(12, 12));
        raiz.setBorder(new EmptyBorder(16, 20, 12, 20));
        raiz.setBackground(FUNDO);
        setContentPane(raiz);

        JPanel cabecalho = new JPanel(new BorderLayout(8, 8));
        cabecalho.setBackground(AZUL);
        cabecalho.setBorder(new EmptyBorder(12, 18, 12, 18));
        JLabel titulo = new JLabel("CLUBE VANTAGENS");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        titulo.setForeground(Color.WHITE);
        cabecalho.add(titulo, BorderLayout.NORTH);
        JLabel subtitulo = new JLabel("Cadastre membros, reconheça a fidelidade e transforme pontos em benefícios.");
        subtitulo.setForeground(new Color(224, 237, 255));
        cabecalho.add(subtitulo, BorderLayout.CENTER);
        resumo.setBorder(new EmptyBorder(6, 0, 0, 0));
        resumo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        resumo.setForeground(Color.WHITE);
        cabecalho.add(resumo, BorderLayout.SOUTH);
        raiz.add(cabecalho, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setOpaque(false);
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        abas.addTab("01  Adesão ao clube", criarCadastro());
        abas.addTab("02  Pontos e benefícios", criarOperacoes());
        centro.add(abas, BorderLayout.NORTH);
        centro.add(criarListagem(), BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setOpaque(false);
        retorno.setOpaque(true);
        retorno.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        retorno.setBorder(new EmptyBorder(10, 12, 10, 12));
        mostrarRetorno("Tudo pronto. Cadastre um membro para começar.", AZUL, new Color(226, 237, 252));
        rodape.add(retorno, BorderLayout.CENTER);
        JLabel aviso = new JLabel("Os dados desta sessão são temporários e serão descartados ao sair.");
        aviso.setForeground(new Color(90, 105, 125));
        aviso.setBorder(new EmptyBorder(8, 0, 0, 0));
        rodape.add(aviso, BorderLayout.SOUTH);
        rodape.add(botao("Sair", this::solicitarSaida), BorderLayout.EAST);
        raiz.add(rodape, BorderLayout.SOUTH);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(36);
        tabela.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(new Color(220, 234, 253));
        tabela.setSelectionForeground(TEXTO);
        tabela.setFillsViewportHeight(true);
        tabela.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabela.setDefaultRenderer(Object.class, new RenderizadorMembros());
        tabela.setDefaultRenderer(Integer.class, new RenderizadorMembros());
        tabela.getColumnModel().getColumn(0).setMaxWidth(58);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(75);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(155);
        tabela.setToolTipText("Selecione uma linha para abrir o extrato. Pode resgatar indica saldo para ao menos um benefício.");
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
        painel.setBackground(Color.WHITE);
        painel.setBorder(new EmptyBorder(10, 12, 10, 12));
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
        painel.setBackground(Color.WHITE);
        painel.setBorder(new EmptyBorder(10, 12, 10, 12));
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
        c.gridx = 0; c.gridy = 3; c.gridwidth = 3;
        saldoBeneficio.setOpaque(true);
        saldoBeneficio.setBorder(new EmptyBorder(6, 10, 6, 10));
        saldoBeneficio.setForeground(AZUL);
        saldoBeneficio.setBackground(new Color(235, 242, 253));
        painel.add(saldoBeneficio, c);
        beneficio.addActionListener(evento -> atualizarPreviaBeneficio());
        id.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent evento) { atualizarPreviaBeneficio(); }
            @Override public void removeUpdate(DocumentEvent evento) { atualizarPreviaBeneficio(); }
            @Override public void changedUpdate(DocumentEvent evento) { atualizarPreviaBeneficio(); }
        });
        return painel;
    }

    private JPanel criarListagem() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setOpaque(false);
        JPanel busca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        busca.setBackground(Color.WHITE);
        busca.setBorder(new EmptyBorder(4, 4, 4, 4));
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
        listagem.setOpaque(false);
        ordem.setForeground(new Color(90, 105, 125));
        ordem.setBorder(new EmptyBorder(0, 10, 0, 0));
        listagem.add(ordem, BorderLayout.NORTH);
        JScrollPane rolagemTabela = new JScrollPane(tabela);
        rolagemTabela.setBorder(BorderFactory.createLineBorder(BORDA));
        rolagemTabela.getViewport().setBackground(Color.WHITE);
        rolagemTabela.setPreferredSize(new Dimension(900, 180));
        listagem.add(rolagemTabela, BorderLayout.CENTER);

        extrato.setEditable(false);
        extrato.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        extrato.setForeground(TEXTO);
        extrato.setBackground(new Color(249, 251, 254));
        extrato.setMargin(new Insets(12, 14, 12, 14));
        extrato.setLineWrap(true);
        extrato.setWrapStyleWord(true);
        extrato.setText("Selecione um membro na listagem ou consulte pelo ID na aba Pontos e benefícios.");
        JScrollPane rolagemExtrato = new JScrollPane(extrato);
        rolagemExtrato.setBorder(BorderFactory.createLineBorder(BORDA));
        JPanel painelExtrato = new JPanel(new BorderLayout(0, 8));
        painelExtrato.setBackground(Color.WHITE);
        painelExtrato.setBorder(new EmptyBorder(10, 10, 10, 10));
        membroConsultado.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        membroConsultado.setForeground(AZUL);
        painelExtrato.add(membroConsultado, BorderLayout.NORTH);
        painelExtrato.add(rolagemExtrato, BorderLayout.CENTER);
        JSplitPane divisao = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listagem, painelExtrato);
        divisao.setBorder(null);
        divisao.setDividerSize(10);
        divisao.setResizeWeight(0.55);
        painel.add(divisao, BorderLayout.CENTER);
        return painel;
    }

    private void campo(JPanel painel, String texto, JComponent componente, int linha) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = linha; c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(3, 6, 3, 12);
        componente.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        componente.setPreferredSize(new Dimension(200, 30));
        JLabel rotulo = new JLabel(texto);
        rotulo.setForeground(TEXTO);
        rotulo.setLabelFor(componente);
        painel.add(rotulo, c);
        c.gridx = 1; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        painel.add(componente, c);
    }

    private JButton botao(String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        botao.setMargin(new Insets(4, 10, 4, 10));
        botao.setBackground(new Color(234, 240, 247));
        botao.setForeground(AZUL);
        if (texto.equals("Cadastrar") || texto.equals("Pesquisar")) {
            botao.setBackground(AZUL);
            botao.setForeground(Color.WHITE);
        } else if (texto.equals("Acumular pontos")) {
            botao.setBackground(VERDE);
            botao.setForeground(Color.WHITE);
        } else if (texto.equals("Resgatar benefício")) {
            botao.setBackground(ROXO);
            botao.setForeground(Color.WHITE);
        }
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
        MembroClube membro = gerenciador.buscarPorId(lerId());
        membroConsultado.setText("Extrato de " + membro.getNome() + "  |  #" + membro.getId()
                + "  |  " + membro.getPlano() + "  |  " + membro.getPontos() + " pontos");
        atualizarPreviaBeneficio();
        extrato.setText(gerenciador.emitirExtrato(membro.getId()));
        extrato.setCaretPosition(0);
        mostrarRetorno("Extrato de " + membro.getNome() + " atualizado.", AZUL, new Color(226, 237, 252));
    }

    private void atualizarListagem() {
        modelo.setRowCount(0);
        for (MembroClube membro : gerenciador.pesquisar(pesquisa.getText(), ranking)) {
            modelo.addRow(new Object[]{membro.getId(), membro.getNome(), membro.getEmail(), membro.getPlano(), membro.getPontos(), situacaoSaldo(membro.getPontos())});
        }
        resumo.setText(gerenciador.getQuantidadeMembros() + " membros  |  " + gerenciador.getTotalPontos()
                + " pontos em circulação  |  Essencial: 1x  ·  Premium: 2x");
        ordem.setText((ranking ? "Ranking: maior saldo primeiro; empate por ID" : "Ordenação: ID crescente")
                + "  |  " + modelo.getRowCount() + " resultado(s)"
                + (modelo.getRowCount() == 0 ? " · Cadastre um membro ou ajuste a pesquisa." : ""));
    }

    private String situacaoSaldo(int saldo) {
        if (saldo == 0) return "Sem pontos";
        if (saldo < Beneficio.CAFE.getCusto()) return "Acumulando";
        return "Pode resgatar";
    }

    private void atualizarPreviaBeneficio() {
        try {
            MembroClube membro = gerenciador.buscarPorId(lerId());
            Beneficio escolhido = (Beneficio) beneficio.getSelectedItem();
            if (escolhido == null) return;
            int faltam = escolhido.getCusto() - membro.getPontos();
            saldoBeneficio.setText(membro.getNome() + " · " + membro.getPontos() + " pontos · "
                    + (faltam > 0 ? "Faltam " + faltam + " pontos para este benefício."
                    : "Saldo suficiente para este benefício."));
            saldoBeneficio.setForeground(faltam > 0 ? AMBAR : VERDE);
            saldoBeneficio.setBackground(faltam > 0 ? new Color(255, 247, 221) : new Color(226, 244, 233));
        } catch (NumberFormatException | CadastroInvalidoException ex) {
            saldoBeneficio.setText("Consulte um membro para conferir o saldo do benefício.");
            saldoBeneficio.setForeground(AZUL);
            saldoBeneficio.setBackground(new Color(235, 242, 253));
        }
    }

    private void mostrarRetorno(String mensagem, Color corTexto, Color corFundo) {
        retorno.setText(mensagem);
        retorno.setForeground(corTexto);
        retorno.setBackground(corFundo);
        retorno.setToolTipText(mensagem);
    }

    // Renderização básica: o texto identifica a situação mesmo sem distinguir as cores.
    private static final class RenderizadorMembros extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                       boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(tabela, valor, selecionada, foco, linha, coluna);
            setBorder(new EmptyBorder(4, 10, 4, 10));
            setFont(tabela.getFont());
            setHorizontalAlignment(coluna == 0 || coluna >= 3 ? CENTER : LEFT);
            setForeground(TEXTO);
            setBackground(selecionada ? tabela.getSelectionBackground()
                    : linha % 2 == 0 ? Color.WHITE : new Color(246, 249, 252));
            if (coluna == 3 || coluna == 5) {
                setFont(tabela.getFont().deriveFont(Font.BOLD));
                if (coluna == 3) {
                    setForeground("Premium".equals(valor) ? ROXO : AZUL);
                    if (!selecionada) setBackground("Premium".equals(valor)
                            ? new Color(243, 235, 252) : new Color(233, 241, 253));
                } else {
                    setForeground("Pode resgatar".equals(valor) ? VERDE
                            : "Acumulando".equals(valor) ? AMBAR : new Color(90, 105, 125));
                    if (!selecionada) setBackground("Pode resgatar".equals(valor)
                            ? new Color(226, 244, 233) : "Acumulando".equals(valor)
                            ? new Color(255, 247, 221) : new Color(237, 241, 246));
                }
            } else if (coluna == 4) {
                setFont(tabela.getFont().deriveFont(Font.BOLD));
            }
            return this;
        }
    }

    @FunctionalInterface
    private interface AcaoTela {
        void executar() throws CadastroInvalidoException, PontuacaoInsuficienteException;
    }

    private void executar(AcaoTela acao) {
        try {
            acao.executar();
        } catch (NumberFormatException ex) {
            mostrarRetorno("Valor inválido. Confira os campos de ID e pontos.", AMBAR, new Color(255, 247, 221));
            JOptionPane.showMessageDialog(this, "Informe números inteiros válidos para ID e pontos (até 2147483647).",
                    "Valor numérico inválido", JOptionPane.WARNING_MESSAGE);
        } catch (CadastroInvalidoException | PontuacaoInsuficienteException ex) {
            mostrarRetorno(ex.getMessage(), AMBAR, new Color(255, 247, 221));
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Operação não realizada", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void informar(String mensagem) {
        mostrarRetorno(mensagem, VERDE, new Color(226, 244, 233));
        JOptionPane.showMessageDialog(this, mensagem, "Clube Vantagens", JOptionPane.INFORMATION_MESSAGE);
    }

    private void solicitarSaida() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja sair? Todos os dados desta execução serão perdidos.", "Encerrar clube",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta == JOptionPane.YES_OPTION) dispose();
    }
}
