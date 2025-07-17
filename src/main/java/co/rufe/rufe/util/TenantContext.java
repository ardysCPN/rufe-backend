package co.rufe.rufe.util;

/**
 * Utility class to store and retrieve the current organization (tenant) ID
 * using a ThreadLocal. This ensures that the organization context is
 * available throughout the request lifecycle, crucial for multi-tenancy.
 */
public class TenantContext {

    private static final ThreadLocal<Long> currentOrganizationId = new ThreadLocal<>();

    public static void setCurrentOrganizationId(Long organizationId) {
        currentOrganizationId.set(organizationId);
    }

    public static Long getCurrentOrganizationId() {
        return currentOrganizationId.get();
    }

    public static void clear() {
        currentOrganizationId.remove();
    }
}
