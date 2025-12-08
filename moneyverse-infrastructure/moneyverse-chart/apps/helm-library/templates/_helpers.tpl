{{/*
Standard labels
*/}}
{{- define "helm-library.labels" -}}
helm.sh/chart: {{ include "helm-library.chart" . }}
{{ include "helm-library.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: moneyverse
{{- end }}

{{/*
Selector labels
*/}}
{{- define "helm-library.selectorLabels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "helm-library.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Helper template for fullname
*/}}
{{- define "helm-library.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{- /*
helm-library.util.merge will merge two YAML templates and output the result.
This takes an array of three values:
- the top context
- the template name of the overrides (destination)
- the template name of the base (source)
*/ -}}
{{- define "helm-library.util.merge" }}
{{- $top := first . }}
{{- $overrides := fromYaml (include (index . 1) $top) | default (dict ) }}
{{- $tpl := fromYaml (include (index . 2) $top) | default (dict ) }}
{{ toYaml (merge $overrides $tpl) }}
{{- end }}

{{- define "wait-for-service" -}}
- name: "wait-for-{{ .name }}"
  image: "busybox:1.36"
  command:
    - "sh"
    - "-c"
    - >-
      until nc -z {{ .host }} {{ .port }};
      do echo "Waiting for {{ .name }}...";
      sleep 3;
      done;
{{- end -}}