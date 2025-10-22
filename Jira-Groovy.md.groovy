package extensions.data.extractors

NEWLINE = System.getProperty("line.separator")
SEPARATOR = "|"
BACKSLASH = "\\"
BACKQUOTE = "`"
LTAG = "<"
RTAG = ">"
ASTERISK = "*"
UNDERSCORE = "_"
LPARENTH = "("
RPARENTH = ")"
LBRACKET = "["
RBRACKET = "]"
TILDE = "~"
LBRACE = "{"
RBRACE = "}"
HASH = "#"
EXCLAMATION = "!"
PLUS = "+"
MINUS = "-"
DOT = "."

def escapeJiraFormatting = { text ->
    def result = text
        .replace(BACKSLASH, BACKSLASH + BACKSLASH)
        .replace(SEPARATOR, BACKSLASH + SEPARATOR)
        .replace(BACKQUOTE, BACKSLASH + BACKQUOTE)
        .replace(ASTERISK, BACKSLASH + ASTERISK)
        .replace(UNDERSCORE, BACKSLASH + UNDERSCORE)
        .replace(LPARENTH, BACKSLASH + LPARENTH)
        .replace(RPARENTH, BACKSLASH + RPARENTH)
        .replace(LBRACKET, BACKSLASH + LBRACKET)
        .replace(RBRACKET, BACKSLASH + RBRACKET)
        .replace(TILDE, BACKSLASH + TILDE)
        .replace(LBRACE, BACKSLASH + LBRACE)
        .replace(RBRACE, BACKSLASH + RBRACE)
        .replace(HASH, BACKSLASH + HASH)
        .replace(EXCLAMATION, BACKSLASH + EXCLAMATION)
        .replace(PLUS, BACKSLASH + PLUS)
        .replace(MINUS, BACKSLASH + MINUS)
        .replace(LTAG, "&lt;")
        .replace(RTAG, "&gt;")
        .replaceAll("\r\n|\r|\n", " ")
        .replaceAll("\t|\b|\f", "")
        .trim()

    // Специальная обработка для {code} - используем простую замену без regex
    result = result.replace("{code}", BACKSLASH + LBRACE + "code" + BACKSLASH + RBRACE)
                  .replace("{CODE}", BACKSLASH + LBRACE + "CODE" + BACKSLASH + RBRACE)

    return result
}

def printRow = { values, firstBold = false, valueToString ->
  OUT.append("|")
  values.eachWithIndex { value, idx ->
    def str = escapeJiraFormatting(valueToString(value))

    OUT.append(" ")
      .append(firstBold && idx == 0 ? "*" : "")
      .append(str)
      .append(firstBold && idx == 0 ? "*" : "")
      .append(" |")
  }
  OUT.append(NEWLINE)
}

if (TRANSPOSED) {
  def values = COLUMNS.collect { new ArrayList<String>([it.name()]) }
  def rowCount = 0
  ROWS.forEach { row ->
    COLUMNS.eachWithIndex { col, i -> values[i].add(FORMATTER.format(row, col)) }
    rowCount++
  }

  // Header row
  OUT.append("||")
  for (int i = 0; i < rowCount; i++) {
    OUT.append(" ||")
  }
  OUT.append(NEWLINE)

  // Data rows
  values.each { printRow(it, true) { it } }
}
else {
  // Header row
  OUT.append("||")
  COLUMNS.each { OUT.append(" " + escapeJiraFormatting(it.name()) + " ||") }
  OUT.append(NEWLINE)

  // Data rows
  ROWS.each { row -> printRow(COLUMNS) { FORMATTER.format(row, it) } }
}