{{- define "moneyverse.postgresql-host" -}}
{{ printf "%s-%s" .Release.Name "postgresql" }}
{{- end }}

{{- define "moneyverse.clickhouse-host" -}}
{{ printf "%s-%s" .Release.Name "clickhouse" }}
{{- end }}

{{- define "moneyverse.keycloak-host" -}}
{{ printf "%s-%s" .Release.Name "keycloak" }}
{{- end }}

{{- define "moneyverse.kafka-host" -}}
{{ printf "%s-%s" .Release.Name "kafka" }}
{{- end }}

{{- define "moneyverse.redis-host" -}}
{{ printf "%s-%s" .Release.Name "redis" }}
{{- end }}

{{- define "moneyverse.currency-management-host" -}}
{{ printf "%s-%s" .Release.Name "currency-management" }}
{{- end }}

{{- define "moneyverse.user-management-host" -}}
{{ printf "%s-%s" .Release.Name "user-management" }}
{{- end }}

{{- define "moneyverse.account-management-host" -}}
{{ printf "%s-%s" .Release.Name "account-management" }}
{{- end }}

{{- define "moneyverse.budget-management-host" -}}
{{ printf "%s-%s" .Release.Name "budget-management" }}
{{- end }}

{{- define "moneyverse.transaction-management-host" -}}
{{ printf "%s-%s" .Release.Name "transaction-management" }}
{{- end }}

{{- define "moneyverse.analytics-host" -}}
{{ printf "%s-%s" .Release.Name "analytics" }}
{{- end }}

{{- define "moneyverse.krakend-host" -}}
{{ printf "%s-%s" .Release.Name "krakend" }}
{{- end }}

{{- define "moneyverse.nginx-host" -}}
{{ printf "%s-%s" .Release.Name "nginx" }}
{{- end }}